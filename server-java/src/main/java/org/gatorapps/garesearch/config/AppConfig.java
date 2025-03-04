package org.gatorapps.garesearch.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String frontendHost;
    @Setter
    @Getter
    private String prodStatus;

    @Value("${app.session.cookie.secret}")
    private String sessionSecretJson;

    private List<String> sessionSecrets;

    public List<String> getSessionSecrets() {
        if (sessionSecrets == null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                sessionSecrets = objectMapper.readValue(sessionSecretJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse session cookie secrets", e);
            }
        }
        return sessionSecrets;
    }
}
