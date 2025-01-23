package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
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
