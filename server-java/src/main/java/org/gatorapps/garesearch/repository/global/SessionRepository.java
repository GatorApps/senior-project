package org.gatorapps.garesearch.repository.global;

import org.gatorapps.garesearch.model.global.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    Optional<Session> findById(String id);
}