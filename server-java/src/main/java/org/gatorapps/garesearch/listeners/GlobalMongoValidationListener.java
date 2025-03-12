package org.gatorapps.garesearch.listeners;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class GlobalMongoValidationListener extends AbstractMongoEventListener<Object> {

    private final Validator validator;

    public GlobalMongoValidationListener(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        Object entity = event.getSource();
        Set<ConstraintViolation<Object>> violations = validator.validate(entity);

//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(violations);
//        }
    }
}
