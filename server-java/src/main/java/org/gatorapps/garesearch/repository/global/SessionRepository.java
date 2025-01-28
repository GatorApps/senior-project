package org.gatorapps.garesearch.repository.global;

import org.gatorapps.garesearch.model.global.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SessionRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Session getSessionData(String sessionString) {
        try {
            return objectMapper.readValue(sessionString, Session.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session data", e);
        }
    }
}
