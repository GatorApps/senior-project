package org.gatorapps.templateapp.repository.garesearch;

import org.gatorapps.templateapp.model.garesearch.Lab;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LabRepository extends MongoRepository<Lab, String> {
    // Custom query methods can be defined here if needed

    boolean existsByLabId(String labId);
}
