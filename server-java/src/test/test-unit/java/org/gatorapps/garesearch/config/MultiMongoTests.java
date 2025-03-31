package org.gatorapps.garesearch.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MultiMongoConfig.class)
public class MultiMongoTests {
    @Autowired
    @Qualifier("globalMongoTemplate")
    private MongoTemplate globalMongoTemplate;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    @Qualifier("accountMongoTemplate")
    private MongoTemplate accountMongoTemplate;

    @Test
    void globalMongoTemplateBean(){
        assertNotNull(globalMongoTemplate);
    }

    @Test
    void garesearchMongoTemplateBean(){
        assertNotNull(garesearchMongoTemplate);
    }

    @Test
    void accountMongoTemplateBean(){
        assertNotNull(accountMongoTemplate);
    }
}
