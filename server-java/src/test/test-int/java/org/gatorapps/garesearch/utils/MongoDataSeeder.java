package org.gatorapps.garesearch.utils;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import org.gatorapps.garesearch.model.garesearch.*;

import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.global.App;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Date;
import java.util.List;


// TODO : fix how dates are uploaded . maybe just reformat jsons
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

    public <T> void insertJsonData(String databaseName, String collectionName, String filePath, Class<T> modelClass) throws IOException {

         System.out.println("dbName: " + databaseName + " collection: " + collectionName + " filepath: " + filePath);

        List<T> data = objectMapper.readValue(
                new ClassPathResource(filePath).getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, modelClass)
        );

        MongoTemplate mongotemp = null;
        switch (databaseName) {
            case "test_global":
                mongotemp = globalMongoTemplate;
                break;
            case "test_account":
                mongotemp = accountMongoTemplate;
                break;
            case "test_garesearch":
                mongotemp = garesearchMongoTemplate;
                break;
        }

        if (mongotemp != null) {
            mongotemp.getMongoDatabaseFactory().getMongoDatabase(databaseName).createCollection(collectionName);

            mongotemp.insertAll(data);

            // useful to see everything uploaded successfully
            // printCollection(mongotemp, databaseName, collectionName);
        }
    }

    public void populateDatabase() throws IOException {
        System.out.println("--------------------------------------------");
        System.out.println("populating");
        System.out.println("--------------------------------------------");

        module.addDeserializer(Date.class, new DateDeserializer());
        module.addDeserializer(org.gatorapps.garesearch.model.garesearch.supportingclasses.User.class, new UserDeserializer());
        module.addDeserializer(String.class, new ObjectIdDeserializer());
        objectMapper.registerModule(module);

        insertJsonData("test_global", "apps", "/data/global/apps.json", App.class);
        insertJsonData("test_account", "users", "/data/account/users.json", User.class);
        insertJsonData("test_garesearch", "applicantprofiles", "/data/garesearch/applicantProfiles.json", ApplicantProfile.class);
        insertJsonData("test_garesearch", "applications", "/data/garesearch/applications.json", Application.class);
        insertJsonData("test_garesearch", "files", "/data/garesearch/files.json", File.class);
        insertJsonData("test_garesearch", "labs", "/data/garesearch/labs.json", Lab.class);
        insertJsonData("test_garesearch", "positions", "/data/garesearch/positions.json", Position.class);
    }


    public void deleteDatabase() {
        globalMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
        accountMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
        garesearchMongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
    }

    private void printCollection(MongoTemplate mongotemp, String databaseName, String collectionName){

        FindIterable<Document> documents = mongotemp.getMongoDatabaseFactory().getMongoDatabase(databaseName).getCollection(collectionName).find();

        // Print each document
        for (Document document : documents) {
            System.out.println(document.toJson());
        }


    }


}
