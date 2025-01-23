package org.gatorapps.templateapp.validators;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This is creating a custom annotation for the labId field using custom validation logic from LabIdExistsValidator **/
@Constraint(validatedBy = LabIdExistsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LabIdExists {
    String message() default "Lab with provided ID does not exist";

    // groups will allow running validator based on something such as Create.class etc
    // Class<?>[] groups() default{};
}
