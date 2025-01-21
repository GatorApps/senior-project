package org.gatorapps.templateapp.service;

import org.gatorapps.templateapp.model.account.User;
import org.gatorapps.templateapp.repository.account.UserRepository;
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

//    // Read
//    public Optional<Application> getApplicationById(String id) {
//        return applicationRepository.findById(id);
//    }
//
//    public List<Application> getAllApplications() {
//        return applicationRepository.findAll();
//    }
//
//    // Update
//    public Application updateApplication(String id, Application updatedApplication) {
//        Optional<Application> optionalApplication = applicationRepository.findById(id);
//        if (optionalApplication.isPresent()) {
//            Application existingApplication = optionalApplication.get();
//            existingApplication.setOpid(updatedApplication.getOpid());
//            existingApplication.setPositionId(updatedApplication.getPositionId());
//            existingApplication.setSubmissionTimeStamp(updatedApplication.getSubmissionTimeStamp());
//            existingApplication.setStatus(updatedApplication.getStatus());
//            return applicationRepository.save(existingApplication);
//        }
//        throw new RuntimeException("Application not found with id: " + id);
//    }
//
//    // Delete
//    public void deleteApplication(String id) {
//        applicationRepository.deleteById(id);
//    }
}
