package org.gatorapps.garesearch.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This is creating a custom annotation for the endDate field using custom validation logic from EndDateValidator **/
@Constraint(validatedBy = PositionIdExistsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositionIdExists {
    String message() default "Position with provided positionId does not exist";

    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {}; 
}
