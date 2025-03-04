package org.gatorapps.garesearch.repository.account;

import org.gatorapps.garesearch.model.account.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByOpid(String opid);
    Optional<User> findByOpid(String opid);
}
