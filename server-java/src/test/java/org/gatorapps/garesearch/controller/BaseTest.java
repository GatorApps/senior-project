package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.utils.MongoDataSeeder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import java.io.IOException;

@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest implements ApplicationContextAware {

    static MongoDataSeeder mongoDataSeeder;

    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:latest");

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    static{
        mongoContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void insertData() throws IOException {
        mongoDataSeeder = context.getBean(MongoDataSeeder.class);
        mongoDataSeeder.populateDatabase();

    }

    @AfterAll
    static void stopContainer() {
        System.out.println("being deleted");
        mongoDataSeeder = context.getBean(MongoDataSeeder.class);
        mongoDataSeeder.deleteDatabase();
    }
}
