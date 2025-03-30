package org.gatorapps.garesearch.constants;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@Component
@TestPropertySource("classpath:application-test.properties")
public class RequestConstants {
    public static String TEST_USER_OPID = "52db512f-44ee-4337-81f0-e8cc595240e8";

    public static String VALID_COOKIE_VALUE = "Bearer 52db512f-44ee-4337-81f0-e8cc595240e8";
    public static final String HEADER_NAME = "GATORAPPS_APP";
    public static final String VALID_HEADER_VALUE = "garesearch";
}
