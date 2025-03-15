package org.gatorapps.garesearch.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** This is creating a custom annotation for the labId field using custom validation logic from LabIdExistsValidator **/
@Constraint(validatedBy = LabIdExistsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface LabIdExists {
    String message() default "Lab with provided ID does not exist";

    // groups will allow running validator based on something such as Create.class etc
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
