package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PositionRepository extends MongoRepository<Position, String> {
    // Custom query methods can be defined here if needed
    Optional<Position> findById(String id);
    boolean existsById(String id);
}
