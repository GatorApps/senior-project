package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PositionRepository extends MongoRepository<Position, String> {
    // Custom query methods can be defined here if needed
    Optional<Position> findById(String id);
    boolean existsById(String id);
}
