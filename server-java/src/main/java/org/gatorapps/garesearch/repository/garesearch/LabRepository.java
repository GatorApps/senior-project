package org.gatorapps.garesearch.repository.garesearch;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabRepository extends MongoRepository<Lab, String> {
    // Custom query methods can be defined here if needed
    Optional<Lab> findById(String id);
    boolean existsById(String id);

    Optional<Lab> findByName(String name);
    boolean existsByName(String name);
}
