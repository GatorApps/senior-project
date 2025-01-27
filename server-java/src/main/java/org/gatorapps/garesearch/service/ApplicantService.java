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

    public ApplicantProfile getProfileById (){
        // TODO: retrieving opid from spring security or something

        String opid = "127ad6f9-a0ff-4e3f-927f-a70b64c542e4";

        return applicantProfileRepository.findByOpid(opid)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }

    public void updateProfileById (ApplicantProfile applicantProfile) throws Exception {
        // TODO : retrieving opid from spring security or something
        // applicantProfile.setOpid(authedUser.opid)

        String opid = "127ad6f9-a0ff-4e3f-927f-a70b64c542e4";

        applicantProfile.setLastUpdateTimeStampToNow();

        // manual command to validate because JPA annotations do not get checked on .save
        validationUtil.validate(applicantProfile);

        try {
            Optional<ApplicantProfile> existingProfile = applicantProfileRepository.findByOpid(applicantProfile.getOpid());

            if (existingProfile.isPresent()) {
                // Update the existing profile (save will overwrite if opid matches)
                applicantProfile.setId(existingProfile.get().getId());
            }

            applicantProfileRepository.save(applicantProfile);
        } catch (Exception e) {
            throw new Exception("Unable to process your request at this time", e);
        }
    }

}
