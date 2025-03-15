package org.gatorapps.garesearch.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.gatorapps.garesearch.validators.LabIdExistsValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class ValidationConfig {
//
//    private static final Validator validator;
//
//    static {
//        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
//            validator = factory.getValidator();
//        }
//    }
//
//    @Bean
//    public Validator validator() {
//        return validator;
//    }
//
//}
