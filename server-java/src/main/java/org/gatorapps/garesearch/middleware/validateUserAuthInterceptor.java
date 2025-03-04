package org.gatorapps.garesearch.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.gatorapps.garesearch.config.AppConfig;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.global.Session;
import org.gatorapps.garesearch.model.global.SessionAttributes;
import org.gatorapps.garesearch.repository.account.UserRepository;
import org.gatorapps.garesearch.repository.global.AppRepository;
import org.gatorapps.garesearch.repository.global.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class ValidateUserAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AppConfig appConfig;

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public ValidateUserAuthInterceptor(UserRepository userRepository, SessionRepository sessionRepository) throws Exception {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public static PublicKey loadPublicKey(String key) throws GeneralSecurityException {
        try {
            String publicKeyPEM = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", ""); // Remove all whitespace and newlines

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC"); // Use "RSA" if it's an RSA key
            return keyFactory.generatePublic(keySpec);
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException("Invalid public key encoding", e);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Accept simulated user auth in dev mode
        if (appConfig.getProdStatus().equals("dev")) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String opid = authHeader.substring(7);  // Remove "Bearer " prefix
                Optional<User> foundUser = userRepository.findByOpid(opid);
                if (foundUser.isPresent()) {
                    request.setAttribute("userAuth", new UserAuth(foundUser.get(), new AuthError("0")));
                } else {
                    request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Invalid user opid")));
                }
                return true;
            }
        }

        // Read GATORAPPS_GLOBAL_SID cookie value
        String globalSIDCookieValue = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("GATORAPPS_GLOBAL_SID")) {
                    globalSIDCookieValue = cookie.getValue();
                }
            }
        }
        if (globalSIDCookieValue == null) {
            request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Missing user auth session")));
            return true;
        }
        // Decode session cookie value
        String decodedGlobalSIDCookieValue = URLDecoder.decode(globalSIDCookieValue, StandardCharsets.UTF_8);
        if (!decodedGlobalSIDCookieValue.startsWith("s:")) {
            request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Invalid user auth session")));
            return true;
        }
        // Remove the "s:" prefix
        decodedGlobalSIDCookieValue = decodedGlobalSIDCookieValue.substring(2);
        // Split session cookie into session ID and signature
        String[] parts = decodedGlobalSIDCookieValue.split("\\.");
        if (parts.length != 2) {
            request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Invalid user auth session")));
            return true;
        }
        String sessionId = parts[0];
        String signature = parts[1];

        // Verify Signature
        if (!verifySignature(sessionId, signature)) {
            request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Invalid user auth session")));
            return true;
        }

        // Fetch session with sessionId
        Optional<Session> foundSession = sessionRepository.findById(sessionId);
        if (foundSession.isEmpty()) {
            request.setAttribute("userAuth", new UserAuth(null, new AuthError(403, "-", "Invalid user auth session")));
            return true;
        }
        Session session = foundSession.get();

        // Parse session attributes string
        String sessionAttributesString = session.getSession();
        SessionAttributes sessionAttributes = objectMapper.readValue(sessionAttributesString, SessionAttributes.class);
        System.out.println(sessionAttributes.getUserAuth().getOpid());

        return true;

//        String opid = userAuth.getOpid();
//        String userAuthToken = userAuth.getToken();
//        if (opid == null || userAuthToken == null) {
//            request.setAttribute("userAuth", new AuthResponse(403, "-", "Incomplete userAuth info"));
//            return true;
//        }
//
//        try {
//            Claims decoded = Jwts.parser()
//                    .setSigningKey(publicKey)
//                    .parseClaimsJws(userAuthToken)
//                    .getBody();
//
//            String decodedOpid = decoded.get("opid", String.class);
//            String decodedSessionID = decoded.get("sessionID", String.class);
//            long decodedSignInTimeStamp = decoded.get("signInTimeStamp", Long.class);
//
//            if (decodedOpid == null || decodedSessionID == null || decodedSignInTimeStamp == 0) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Incomplete userAuthToken"));
//                return true;
//            }
//
//            if (!decodedOpid.equals(opid)) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Token and client opid mismatch"));
//                return true;
//            }
//
//            if (!decodedSessionID.equals(request.getSession().getId())) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Token and client sessionID mismatch"));
//                return true;
//            }
//
//            Optional<User> foundUser = userRepository.findByOpid(decodedOpid);
//            if (foundUser.isEmpty()) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Invalid user opid in userAuthToken"));
//                return true;
//            }
//
//            User user = foundUser.get();
//            boolean sessionExists = user.getSessions().stream()
//                    .anyMatch(session -> session.getSessionID().equals(decodedSessionID) &&
//                            session.getSignInTimeStamp().getTime() == decodedSignInTimeStamp);
//
//            if (!sessionExists) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Client and user sessionID mismatch"));
//                return true;
//            }
//
//            long currentTimestamp = System.currentTimeMillis() / 1000;
//            if (decoded.getExpiration() != null && decoded.getExpiration().before(new Date(currentTimestamp * 1000))) {
//                request.setAttribute("userAuth", new AuthResponse(403, "-", "Auth session has expired", user));
//                return true;
//            }
//
//            request.setAttribute("userAuth", new AuthResponse(user));
//            return true;
//
//        } catch (ExpiredJwtException e) {
//            request.setAttribute("userAuth", new AuthResponse(403, "-", "Auth session has expired"));
//            return true;
//        } catch (SignatureException e) {
//            request.setAttribute("userAuth", new AuthResponse(403, "-", "Invalid userAuthToken"));
//            return true;
//        } catch (Exception e) {
//            request.setAttribute("userAuth", new AuthResponse(500, "-", "Unable to validate user auth session"));
//            return true;
//        }
}

    private boolean verifySignature(String sessionId, String signature) {
        for (String secret : appConfig.getSessionSecrets()) {
            if (verifyWithSecret(sessionId, signature, secret)) {
                return true;
            }
        }
        return false;
    }

    private boolean verifyWithSecret(String sessionId, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmac = mac.doFinal(sessionId.getBytes());
            String expectedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(hmac).replace("-", "+").replace("_", "/");
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    // Helper classes
    @Getter
    public static class AuthError {
        private final int status;
        private final String errCode;
        private final String errMsg;
        private final User expiredUser;

        public AuthError(int status, String errCode, String errMsg, User expiredUser) {
            this.status = status;
            this.errCode = errCode;
            this.errMsg = errMsg;
            this.expiredUser = expiredUser;
        }

        public AuthError(int status, String errCode, String errMsg) {
            this(status, errCode, errMsg, null);
        }

        public AuthError(String errCode) {
            this(-1, "0", null, null);
        }
    }

    @Getter
    public static class UserAuth {
        private final User authedUser;
        private final AuthError authError;

        public UserAuth(User authedUser, AuthError authError) {
            this.authedUser = authedUser;
            this.authError = authError;
        }
    }
}
