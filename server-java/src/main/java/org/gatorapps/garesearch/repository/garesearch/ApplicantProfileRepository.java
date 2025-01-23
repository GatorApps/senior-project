package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicantProfileRepository extends MongoRepository<ApplicantProfile, String> {
    // Custom query methods can be defined here if needed

}
