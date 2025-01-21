package org.gatorapps.templateapp.repository.account;

import org.gatorapps.templateapp.model.account.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // Custom query methods can be defined here if needed
}
