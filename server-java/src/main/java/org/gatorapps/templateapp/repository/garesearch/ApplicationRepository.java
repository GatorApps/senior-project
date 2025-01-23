package org.gatorapps.templateapp.repository.garesearch;

import org.gatorapps.templateapp.model.garesearch.Application;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    // Custom query methods can be defined here if needed

}
