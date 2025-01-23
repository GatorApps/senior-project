package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
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
