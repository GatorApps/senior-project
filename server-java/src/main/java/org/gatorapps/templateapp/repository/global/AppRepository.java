package org.gatorapps.templateapp.repository.global;

import org.gatorapps.templateapp.model.global.App;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppRepository extends MongoRepository<App, String> {
    // Custom query methods can be defined here if needed
}
