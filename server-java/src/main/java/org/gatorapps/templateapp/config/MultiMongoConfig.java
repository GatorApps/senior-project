package org.gatorapps.templateapp.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MultiMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${app.prod-status}")
    private String prodStatus;

    @Value("${app.database.global}")
    private String globalDbName;

    @Value("${app.database.account}")
    private String accountDbName;

    @Value("${app.database.garesearch}")
    private String garesearchDbName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean(name = "globalMongoTemplate")
    public MongoTemplate globalMongoTemplate() {
        return new MongoTemplate(mongoClient(), prodStatus.equals("prod") ? globalDbName : "dev_" + globalDbName);
    }

    @Bean(name = "accountMongoTemplate")
    public MongoTemplate accountMongoTemplate() {
        return new MongoTemplate(mongoClient(), prodStatus.equals("prod") ? accountDbName : "dev_" + accountDbName);
    }

    @Bean(name = "garesearchMongoTemplate")
    public MongoTemplate garesearchMongoTemplate() {
        return new MongoTemplate(mongoClient(), prodStatus.equals("prod") ? garesearchDbName : "dev_" + garesearchDbName);
    }
}
