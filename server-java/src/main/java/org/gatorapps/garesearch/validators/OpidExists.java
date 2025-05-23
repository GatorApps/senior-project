package org.gatorapps.garesearch.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This is creating a custom annotation for the opid field using custom validation logic from OpidExistsValidator **/
@Constraint(validatedBy = OpidExistsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OpidExists {
    String message() default "User with provided opid does not exist";

    Class<?>[] groups() default {};  // Ensure this is added
    Class<? extends Payload>[] payload() default {};
}
