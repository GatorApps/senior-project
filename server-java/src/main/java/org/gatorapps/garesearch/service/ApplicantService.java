package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicantService {
    @Autowired
    ApplicantProfileRepository applicantProfileRepository;

    // can also call mongo template way for extra complex queries and then use Query builder stuff
//    @Autowired
//    @Qualifier("garesearchMongoTemplate")
//    private MongoTemplate garesearchMongoTemplate;

    public ApplicantProfile getProfileById (){
        // TODO: retrieving opid from spring security or something

        String id = "4d9c6082-c107-4828-95bf-d998953f8f80";

        return applicantProfileRepository.findByOpid(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }

    public void updateProfileById (ApplicantProfile applicantProfile) throws Exception {
        // TODO : retrieving opid from spring security or something
        // applicantProfile.setOpid(authedUser.opid)

        applicantProfile.setLastUpdateTimeStampToNow();

        try {
            applicantProfileRepository.save(applicantProfile);
        } catch (Exception e) {
            throw new Exception("Unable to process your request at this time", e);
        }
    }


}
