package org.gatorapps.garesearch.model.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Session {
    private CookieData cookie;
    private UserAuthData userAuth;

    @Data
    public static class CookieData {
        private long originalMaxAge;
        private String expires;
        private boolean httpOnly;
        private String domain;
        private String path;
    }

    @Data
    public static class UserAuthData {
        private String opid;
        private String token;
    }
}
