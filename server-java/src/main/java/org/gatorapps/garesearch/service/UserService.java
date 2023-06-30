package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.repository.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Read: Get a User by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Read: Get all Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update: Update a User by ID
    public User updateUser(String id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Update fields
            existingUser.setOpid(updatedUser.getOpid());
            existingUser.setRegisterTimestamp(updatedUser.getRegisterTimestamp());
            existingUser.setRoles(updatedUser.getRoles());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setNickname(updatedUser.getNickname());
            existingUser.setEmails(updatedUser.getEmails());
            existingUser.setSessions(updatedUser.getSessions());

            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found with id: " + id);
    }

//    // Delete: Delete a User by ID
//    public void deleteUser(String id) {
//        userRepository.deleteById(id);
//    }
}
