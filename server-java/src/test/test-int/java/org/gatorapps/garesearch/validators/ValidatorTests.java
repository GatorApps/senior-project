package org.gatorapps.garesearch.validators;

import org.gatorapps.garesearch.controller.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
public class ValidatorTests extends BaseTest {
    @Autowired
    private LabIdExistsValidator labIdExistsValidator;
    @Autowired
    private PositionIdExistsValidator positionIdExistsValidator;
    @Autowired
    private OpidExistsValidator opidExistsValidator;


    @Test
    void labIdExists() {
        assertTrue(labIdExistsValidator.isValid("99dcf5a77621f49532e47b52", null));
    }

    @Test
    void labIdExists_not() {
        assertFalse(labIdExistsValidator.isValid("111111111111111111111111", null));
    }

    @Test
    void positionIdExists() {
        assertTrue(positionIdExistsValidator.isValid("d162c110ed0a40bea3393855", null));
    }

    @Test
    void positionIdExists_not() {
        assertFalse(positionIdExistsValidator.isValid("111111111111111111111111", null));
    }

    @Test
    void opidExists() {
        assertTrue(opidExistsValidator.isValid("52db512f-44ee-4337-81f0-e8cc595240e8", null));
    }

    @Test
    void opidExists_not() {
        assertFalse(opidExistsValidator.isValid("111111111111111111111111", null));
    }

}
