package org.gatorapps.garesearch.repository.account;

import org.gatorapps.garesearch.model.account.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom query methods can be defined here if needed

    boolean existsByOpid(String opid);
}
