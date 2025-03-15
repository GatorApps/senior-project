package org.gatorapps.garesearch.repository.garesearch;

import org.gatorapps.garesearch.model.garesearch.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findById(String id);
    boolean existsById(String id);
}
