package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicantService {
    @Autowired
    ApplicantProfileRepository applicantProfileRepository;

    public Optional<ApplicantProfile> getProfileById (String id){
        // TODO

        return applicantProfileRepository.findById(id);
    }

    public Optional<ApplicantProfile> updateProfileById (ApplicantProfile applicantProfile){
        // TODO

        return Optional.of(applicantProfileRepository.save(applicantProfile));
    }

}
