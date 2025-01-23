package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Lab;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LabRepository extends MongoRepository<Lab, String> {
    // Custom query methods can be defined here if needed

    boolean existsById(String id);
}
