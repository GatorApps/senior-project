package org.gatorapps.templateapp.service;

import org.gatorapps.templateapp.model.garesearch.ApplicantProfile;
import org.gatorapps.templateapp.model.garesearch.Application;
import org.gatorapps.templateapp.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.templateapp.repository.garesearch.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired
    ApplicationRepository applicationRepository;

    public Optional<Application> getStudentApplications (String id){
        // TODO

        return applicationRepository.findById(id);
    }

    public Optional<Application> submitApplication (Application application){
        // TODO

        return Optional.of(applicationRepository.save(application));
    }

}
