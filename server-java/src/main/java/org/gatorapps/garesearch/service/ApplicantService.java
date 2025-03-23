package org.gatorapps.garesearch.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicantService {
    @Autowired
    ApplicantProfileRepository applicantProfileRepository;

    @Autowired
    private ValidationUtil validationUtil;

    public ApplicantProfile getProfileById (String opid){
        return applicantProfileRepository.findByOpid(opid)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }

    public void updateProfileById (ApplicantProfile applicantProfile) throws Exception {
        try {
            applicantProfileRepository.save(applicantProfile);
        } catch (Exception e) {
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}
