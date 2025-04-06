package org.gatorapps.garesearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

import java.net.URI;

@Configuration
@Profile({"dev-local", "dev", "prod"})
public class AwsConfig {
    @Value("${aws.accessKeyId}") String accessKeyId;
    @Value("${aws.secretKey}") String secretKey;
    @Value("${aws.s3.region}") String region;

    @Value("${aws.ses.region}") String sesRegion;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }


    @Bean
    public SesClient sesClient() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        return SesClient.builder()
                .region(Region.of(sesRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
