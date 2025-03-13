package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    // Custom query methods can be defined here if needed
    Optional<Application> findByOpidAndId(String opid, String id);
    Optional<Application> findByOpidAndPositionId(String opid, String positionId);
    boolean existsByOpidAndPositionId(String opid, String positionId);
}
