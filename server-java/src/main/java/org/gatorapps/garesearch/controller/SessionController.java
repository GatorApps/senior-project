package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.model.global.Session;
import org.gatorapps.garesearch.repository.global.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/get-session")
    public String getSession() {
        // Mock: Retrieve session from MongoDB (replace with actual MongoDB query)
        String sessionString = "{\"cookie\":{\"originalMaxAge\":43200000,\"expires\":\"2025-01-29T06:34:52.993Z\",\"httpOnly\":true,\"domain\":\".gatorapps.org\",\"path\":\"/\"},\"userAuth\":{\"opid\":\"4d9c6082-c107-4828-95bf-d998953f8f80\",\"token\":\"token-value\"}}";

        Session session = sessionRepository.getSessionData(sessionString);
        return "User Auth: " + session.getUserAuth().getOpid();
    }
}
