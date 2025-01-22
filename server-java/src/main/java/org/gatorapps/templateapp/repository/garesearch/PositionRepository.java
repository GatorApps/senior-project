package org.gatorapps.templateapp.repository.garesearch;

import org.gatorapps.templateapp.model.garesearch.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PositionRepository extends MongoRepository<Position, String> {
    // Custom query methods can be defined here if needed

    boolean existsByPositionId(String positionId);
}
