package org.gatorapps.garesearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

@Configuration
@EnableMongoHttpSession
public class MongoSessionConfig {
    @Bean
    public MongoIndexedSessionRepository sessionRepository(MongoOperations mongoOperations) {
        return new MongoIndexedSessionRepository(mongoOperations);
    }
}
