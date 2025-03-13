package org.gatorapps.garesearch.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private FileRepository fileRepository;

    private final AmazonS3 s3Client;
    private static final Tika tika = new Tika();

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(
            @Value("${aws.accessKeyId}") String accessKeyId,
            @Value("${aws.secretKey}") String secretKey,
            @Value("${aws.s3.region}") String region
    ) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

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

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(bucketName, fileS3Path, file.getInputStream(), metadata);

        File uploadedFile = new File(uploaderOpid, category, file.getOriginalFilename(), fileS3Path);
        fileRepository.save(uploadedFile);

        return uploadedFile;
    }

    public ResponseEntity<InputStreamResource> downloadFile(File file) throws IOException {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, file.getS3Path()));

        // Prepare response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, s3Object.getObjectMetadata().getContentType());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }
}
