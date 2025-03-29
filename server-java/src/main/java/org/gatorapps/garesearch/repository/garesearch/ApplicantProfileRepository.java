package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ApplicantProfileRepository extends MongoRepository<ApplicantProfile, String> {
    Optional<ApplicantProfile> findByOpid(String opid);
}
