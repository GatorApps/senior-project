package org.gatorapps.garesearch.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

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

    public String uploadFile(MultipartFile file, List<String> allowedTypes, Long maxSize) throws IOException {
        // Check file size if maxSize is provided
        if (maxSize != null && file.getSize() > maxSize) {
            throw new IOException("File size exceeds the allowed limit of " + (maxSize / 1048576) + "MB");
        }

        // Check file type if allowedTypes is provided
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            String detectedType = tika.detect(file.getInputStream());
            if (!allowedTypes.contains(detectedType)) {
                throw new IOException("Invalid file type. Allowed types: " + allowedTypes);
            }
        }

        String fileName = "uploads/" + UUID.randomUUID();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);

        return s3Client.getUrl(bucketName, fileName).toString(); // Return file URL
    }

}
