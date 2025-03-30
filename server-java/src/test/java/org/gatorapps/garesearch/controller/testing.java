package org.gatorapps.garesearch.controller;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.FileInputStream;
import java.io.IOException;
@TestPropertySource("classpath:application-test.properties")
public class testing {


    @Test
    void testClass() throws IOException {
        String filepath = "/data/garesearch/test.pdf";

        MockMultipartFile file = new MockMultipartFile("resume", "test_resume.pdf", "application/pdf", new ClassPathResource(filepath).getInputStream());



        Tika tika = new Tika();
        String detectedType = tika.detect(file.getInputStream());
        System.out.println(detectedType);
    }

}
