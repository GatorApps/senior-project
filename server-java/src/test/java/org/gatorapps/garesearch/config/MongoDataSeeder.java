package org.gatorapps.garesearch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.utils.DateDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@TestPropertySource("classpath:application-test.properties")
public class MongoDataSeeder {
    @Autowired
    @Qualifier("globalMongoTemplate")
    private MongoTemplate globalMongoTemplate;

    @Autowired
    @Qualifier("garesearchMongoTemplate")
    private MongoTemplate garesearchMongoTemplate;

    @Autowired
    @Qualifier("accountMongoTemplate")
    private MongoTemplate accountMongoTemplate;

    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();



    public void insertJsonData(String databaseName, String collectionName, String filePath) throws IOException {
        List<Map<String, Object>> data = objectMapper.readValue(
                new ClassPathResource(filePath).getInputStream(),
                new TypeReference<>() {}
        );

        for (Map<String, Object> doc : data){
            MongoTemplate mongotemp = null;
            int count = 0;
            switch(databaseName) {
                case "test_global":
                    mongotemp = globalMongoTemplate;
                case "test_account":
                    mongotemp = accountMongoTemplate;
                case "test_garesearch":
                    mongotemp = garesearchMongoTemplate;
            }

            ObjectId objId = new ObjectId();
            if (doc.containsKey("_id") && doc.get("_id") instanceof Map){
                Map<String, Object> idMap = (Map<String, Object>) doc.get("_id");
                System.out.println("collection: " + collectionName + "  id: " + idMap.get("$oid").toString());
                if (idMap.containsKey("$oid")){
                    objId = new ObjectId(idMap.get("$oid").toString());
                }
            }

            doc.remove("_id");
            doc.put("_id", objId);

            if (mongotemp != null) {
                mongotemp.getMongoDatabaseFactory().getMongoDatabase(databaseName)
                        .getCollection(collectionName)
                        .insertOne(new Document(doc));
            }
        }

    }

    public void populateDatabase() throws IOException {
        module.addDeserializer(Date.class, new DateDeserializer());
        objectMapper.registerModule(module);

        insertJsonData("test_global", "apps", "/data/global/apps.json");
        insertJsonData("test_account", "users", "/data/account/users.json");
        insertJsonData("test_garesearch", "applicantprofiles", "/data/garesearch/applicantProfiles.json");
        insertJsonData("test_garesearch", "applications", "/data/garesearch/applications.json");
        // insertJsonData("test_garesearch", "files", "/data/garesearch/files.json");

        insertJsonData("test_garesearch", "labs", "/data/garesearch/labs.json");
        insertJsonData("test_garesearch", "positions", "/data/garesearch/positions.json");
    }


    public void deleteDatabase() {
        globalMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
        accountMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
        garesearchMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
    }
}
