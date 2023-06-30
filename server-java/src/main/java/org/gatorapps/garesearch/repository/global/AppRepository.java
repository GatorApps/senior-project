package org.gatorapps.garesearch.repository.global;

import org.gatorapps.garesearch.model.global.App;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppRepository extends MongoRepository<App, String> {
    // Custom query methods can be defined here if needed
}
