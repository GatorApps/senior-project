package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.MongoDataSeeder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

@SpringBootTest
@Testcontainers
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    @Autowired
    static MongoDataSeeder mongoDataSeeder;

    static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        mongoContainer.start();
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }


    @BeforeAll
    void insertData() throws IOException {
        mongoDataSeeder.populateDatabase();
    }


    @AfterAll
    static void stopContainer() {
        mongoDataSeeder.deleteDatabase();

        mongoContainer.stop();
    }


}
