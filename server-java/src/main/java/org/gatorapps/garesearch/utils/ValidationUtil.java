package org.gatorapps.garesearch.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

/** UTIL to manually validate an entity
 * must manually validate sometimes becasue JPA annotations do not get checked on .save
 */
@Component
public class ValidationUtil {

    @Autowired
    private Validator validator;

    public void validate(Object object) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }


}
