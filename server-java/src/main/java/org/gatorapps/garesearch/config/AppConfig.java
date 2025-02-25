package org.gatorapps.garesearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String frontendHost;

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFrontendHost() { return frontendHost; }
    public void setFrontendHost(String frontendHost) { this.frontendHost = frontendHost; }
}
