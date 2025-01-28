package org.gatorapps.garesearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.model.global.Session;
import org.gatorapps.garesearch.utils.SessionUtil;

import java.util.UUID;

public class SessionManager {
    private static final String[] SECRETS = {"a", "b", "c"};

    public static String createNewSession(Session sessionData) throws Exception {
        String sessionId = UUID.randomUUID().toString();
        String signedSessionId = SessionUtil.sign(sessionId, SECRETS[0]);
        String sessionJson = new ObjectMapper().writeValueAsString(sessionData);

        storeSessionInMongoDB(signedSessionId, sessionJson);
        return signedSessionId;
    }

    private static void storeSessionInMongoDB(String sessionId, String sessionJson) {
        // Implement MongoDB storage logic
    }
}
