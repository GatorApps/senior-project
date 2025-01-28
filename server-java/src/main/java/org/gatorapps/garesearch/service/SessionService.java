package org.gatorapps.garesearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.model.global.Session;

public class SessionService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Session parseSession(String sessionJson) throws Exception {
        return objectMapper.readValue(sessionJson, Session.class);
    }

    public static String updateSession(Session sessionData) throws Exception {
        return objectMapper.writeValueAsString(sessionData);
    }
}
