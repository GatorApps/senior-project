package org.gatorapps.garesearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.gatorapps.garesearch.repository.garesearch",
        mongoTemplateRef = "garesearchMongoTemplate"
)
public class GaresearchMongoConfig {
}
