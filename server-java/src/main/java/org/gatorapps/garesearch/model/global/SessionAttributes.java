package org.gatorapps.garesearch.model.global;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties automatically
public class SessionAttributes {
    private CookieAttributes cookie;
    private UserAuthAttributes userAuth;

    // Store additional attributes here
    private Map<String, Object> additionalAttributes = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalAttributes(String key, Object value) {
        additionalAttributes.put(key, value);
    }

    // Helper classes
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CookieAttributes {
        private long originalMaxAge;
        private String expires;
        private boolean httpOnly;
        private String domain;
        private String path;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserAuthAttributes {
        private String opid;
        private String token;
    }
}