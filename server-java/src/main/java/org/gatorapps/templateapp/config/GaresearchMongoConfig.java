package org.gatorapps.templateapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.gatorapps.templateapp.repository.garesearch",
        mongoTemplateRef = "globalMongoTemplate"
)
public class GaresearchMongoConfig {
}
