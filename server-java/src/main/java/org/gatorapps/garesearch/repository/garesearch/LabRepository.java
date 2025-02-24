package org.gatorapps.garesearch.repository.garesearch;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LabRepository extends MongoRepository<Lab, String> {
    // Custom query methods can be defined here if needed
    Optional<Lab> findById(String id);
    boolean existsById(String id);
}
