package org.gatorapps.templateapp.service;

import org.gatorapps.templateapp.model.global.App;
import org.gatorapps.templateapp.repository.global.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    @Autowired
    private AppRepository appRepository;

    // Create
    public App createApp(App app) {
        return appRepository.save(app);
    }

    // Read
    public Optional<App> getAppById(String id) {
        return appRepository.findById(id);
    }

    public List<App> getAllApps() {
        return appRepository.findAll();
    }

    // Update
    public App updateApp(String id, App updatedApp) {
        Optional<App> optionalApp = appRepository.findById(id);
        if (optionalApp.isPresent()) {
            App existingApp = optionalApp.get();
            existingApp.setCode(updatedApp.getCode());
            existingApp.setName(updatedApp.getName());
            existingApp.setDisplayName(updatedApp.getDisplayName());
            existingApp.setOrigins(updatedApp.getOrigins());
            existingApp.setUserInfoScope(updatedApp.getUserInfoScope());
            existingApp.setAuthOptions(updatedApp.getAuthOptions());
            existingApp.setAlert(updatedApp.getAlert());
            return appRepository.save(existingApp);
        }
        throw new RuntimeException("App not found with id: " + id);
    }

    // Delete
    public void deleteApp(String id) {
        appRepository.deleteById(id);
    }
}
