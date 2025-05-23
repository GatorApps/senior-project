package org.gatorapps.garesearch.controller;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.gatorapps.garesearch.utils.MongoDataSeeder;
import org.gatorapps.garesearch.utils.S3Init;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.ses.SesClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public abstract class BaseTest implements ApplicationContextAware {
    static MongoDataSeeder mongoDataSeeder;
    static S3Init s3Init;

    @Autowired
    private SesClient sesClient;

    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:latest");

    static LocalStackContainer container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.SES)
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withPortBindings(new PortBinding(
                    Ports.Binding.bindPort(4566), new ExposedPort(4566)
            )));


    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    static{
        mongoContainer.start();
        container.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
        //registry.add("cloud.aws.s3.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3));
    }

    @BeforeAll
    static void insertData() throws IOException {
        mongoDataSeeder = context.getBean(MongoDataSeeder.class);
        s3Init = context.getBean(S3Init.class);
        mongoDataSeeder.populateDatabase();
        s3Init.createContainer();
    }

    @AfterAll
    static void stopContainer() {
        System.out.println("being deleted");
        mongoDataSeeder = context.getBean(MongoDataSeeder.class);
        mongoDataSeeder.deleteDatabase();
    }
}
