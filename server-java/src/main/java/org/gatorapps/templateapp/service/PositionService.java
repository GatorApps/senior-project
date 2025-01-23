package org.gatorapps.templateapp.service;

import org.gatorapps.templateapp.model.garesearch.ApplicantProfile;
import org.gatorapps.templateapp.model.garesearch.Lab;
import org.gatorapps.templateapp.model.garesearch.Position;
import org.gatorapps.templateapp.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.templateapp.repository.garesearch.LabRepository;
import org.gatorapps.templateapp.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    public Optional<Position> getPublicProfile (String id){
        // TODO

        return positionRepository.findById(id);
    }

    public Optional<Position> getPositionProfile (String id){
        // TODO

        return positionRepository.findById(id);
    }


    public Optional<Position> createProfile (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

    public Optional<Position> updateProfile (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

}
