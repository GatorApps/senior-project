package org.gatorapps.garesearch.config;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "app.name=MyApp",
        "app.frontendHost=https://frontend.example.com",
        "app.prodStatus=active",
        "app.session.cookie.secret=[\"secret1\", \"secret2\"]"
})
@EnableConfigurationProperties(AppConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppConfigTests {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    @Order(1)
    void appConfigBean(){
        assertNotNull(appConfig);
    }

    @Test
    @Order(2)
    void testPropertiesLoaded(){
        assertEquals("MyApp", appConfig.getName());
        assertEquals("https://frontend.example.com", appConfig.getFrontendHost());
        assertEquals("active", appConfig.getProdStatus());
    }

    @Test
    @Order(3)
    void sessionSecretsParsing_Valid(){
        List<String> sessionSecrets = appConfig.getSessionSecrets();

        assertEquals(2, sessionSecrets.size());
        assertEquals("secret1", sessionSecrets.get(0));
        assertEquals("secret2", sessionSecrets.get(1));
    }

    @Test
    @Order(4)
    void sessionSecretsParsing_EmptyProperty(){
        System.setProperty("app.session.cookie.secret", "");
        appConfig.getSessionSecrets().clear();

        List<String> sessionSecrets = appConfig.getSessionSecrets();

        assertEquals(0, sessionSecrets.size());
    }

}
