package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LabService {
    @Autowired
    LabRepository labRepository;

    public Lab getPublicProfile (String id){
        return labRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }

    public Optional<Lab> getProfile (String id){
        // TODO

        return labRepository.findById(id);
    }


    public Optional<Lab> createProfile (Lab lab){
        // TODO

        return Optional.of(labRepository.save(lab));
    }

    public Optional<Lab> updateProfile (Lab lab){
        // TODO

        return Optional.of(labRepository.save(lab));
    }
}
