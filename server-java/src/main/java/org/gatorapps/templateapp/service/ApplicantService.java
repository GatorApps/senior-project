package org.gatorapps.templateapp.service;

import org.gatorapps.templateapp.model.garesearch.ApplicantProfile;
import org.gatorapps.templateapp.repository.garesearch.ApplicantProfileRepository;
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
