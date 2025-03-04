package org.gatorapps.garesearch.repository.global;

import org.gatorapps.garesearch.model.global.App;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppRepository extends MongoRepository<App, String> {
    Optional<App> findByName(String name);
}
