package org.gatorapps.garesearch.service;

import org.apache.tika.Tika;
import org.gatorapps.garesearch.exception.FileValidationException;
import org.gatorapps.garesearch.model.garesearch.File;
import org.gatorapps.garesearch.repository.garesearch.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private S3Client s3Client;
    private static final Tika tika = new Tika();

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public File uploadFile(MultipartFile file, List<String> allowedTypes, Long maxSize, String S3PathPrefix, String uploaderOpid, String category) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("No file provided");
        }

        // Check file size if maxSize is provided
        if (maxSize != null && file.getSize() > maxSize) {
            throw new FileValidationException("File size exceeds the allowed limit of " + (maxSize / 1048576) + "MB");
        }

        // Check file type if allowedTypes is provided
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            // Check file extension
            String filename = file.getOriginalFilename();
            if (filename != null && filename.contains(".")) {
                String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                if (!allowedTypes.contains(fileExtension)) {
                    throw new FileValidationException("Invalid file type. Allowed type%s: %s".formatted(allowedTypes.size() > 1 ? "s" : "", String.join(", ", allowedTypes)));
                }
            }
            // Check file MIME type
            // Convert allowedTypes to MIME types
            List<String> allowedMimeTypes = new ArrayList<>(allowedTypes);
            allowedMimeTypes.replaceAll(type -> switch (type) {
                case "jpg", "jpeg" -> "image/jpeg";
                case "png" -> "image/png";
                case "pdf" -> "application/pdf";
                case "doc" -> "application/msword";
                case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                case "txt" -> "text/plain";
                default -> type;
            });
            String detectedType = tika.detect(file.getInputStream());
            if (!allowedMimeTypes.contains(detectedType)) {
                throw new FileValidationException("Invalid file type. Allowed type%s: %s".formatted(allowedTypes.size() > 1 ? "s" : "", String.join(", ", allowedTypes)));
            }
        }

        String fileS3Path = (S3PathPrefix != null ? S3PathPrefix : "") + UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileS3Path)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        File uploadedFile = new File(uploaderOpid, category, file.getOriginalFilename(), fileS3Path);
        fileRepository.save(uploadedFile);

        return uploadedFile;
    }

    public ResponseEntity<InputStreamResource> downloadFile(File file) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getS3Path())
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);


        // Prepare response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, s3Object.response().contentType());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.response().contentLength())
                .body(new InputStreamResource(s3Object));
    }
}
