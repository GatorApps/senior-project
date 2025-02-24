package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.exception.ResourceNotFoundException;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;

    public Position getPublicPosting (String id){
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERR_RESOURCE_NOT_FOUND", "Unable to process your request at this time"));
    }


    public Optional<Position> getPosting (String id){
        // TODO

        return positionRepository.findById(id);
    }


    public Optional<Position> createPosting (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

    public Optional<Position> updatePosting (Position position){
        // TODO

        return Optional.of(positionRepository.save(position));
    }

}
