package org.gatorapps.garesearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@Profile({"dev", "prod"})
public class AwsConfig {
    @Value("${aws.accessKeyId}") String accessKeyId;
    @Value("${aws.secretKey}") String secretKey;
    @Value("${aws.s3.region}") String region;

    @Bean
    public S3Client s3Client(){

        System.out.println("accessKeyId: " + accessKeyId);
        System.out.println("secretKey: " + secretKey);
        System.out.println("region: " + region);

        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

}
