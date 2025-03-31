package org.gatorapps.garesearch.config;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@Profile({"test"})
public class AwsTestConfig {
    @Value("${aws.accessKeyId}") String accessKeyId;
    @Value("${aws.secretKey}") String secretKey;
    @Value("${aws.s3.region}") String region;
    @Value("${aws.s3.endpoint}") String endpoint;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
