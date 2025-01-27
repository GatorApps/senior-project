package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApplicantProfileRepository extends MongoRepository<ApplicantProfile, String> {

    Optional<ApplicantProfile> findByOpid(String opid);

}
