package org.gatorapps.garesearch.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CorsConfigTests {

    @Autowired
    private CorsFilter corsFilter;

    @Test
    void corsFilterBean(){
        assertNotNull(corsFilter);
    }

}
