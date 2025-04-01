package org.gatorapps.garesearch.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Component
@TestPropertySource("classpath:application-test.properties")
public class S3Init {

    @Autowired
    private S3Client s3Client;

    public void createContainer() {
        try {
            // Check if container exists
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket("testbucket")
                    .build();
            s3Client.headBucket(headBucketRequest);
        } catch (S3Exception e){
            // If container dne, create it
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket("testbucket").build();
            s3Client.createBucket(bucketRequest);
        }

    }
}
