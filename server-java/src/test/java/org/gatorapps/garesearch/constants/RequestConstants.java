package org.gatorapps.garesearch.constants;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockCookie;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@Component
@TestPropertySource("classpath:application-test.properties")
public class RequestConstants {
    @Value("${test.user.opid}")
    public String testUserOpid;


    @Value("${test.user.cookie}")
    public static String testUserCookie;
    public static String VALID_COOKIE_VALUE;
    public static String TEST_USER_OPID;
    public static final String HEADER_NAME = "GATORAPPS_APP";
    public static final String VALID_HEADER_VALUE = "garesearch";

    @PostConstruct
    public void init() {
        VALID_COOKIE_VALUE = "Bearer " + testUserOpid;
        TEST_USER_OPID = testUserOpid;
    }
}
