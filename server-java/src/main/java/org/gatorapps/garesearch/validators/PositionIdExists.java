package org.gatorapps.garesearch.validators;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This is creating a custom annotation for the endDate field using custom validation logic from EndDateValidator **/
@Constraint(validatedBy = EndDateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositionIdExists {
    String message() default "Position with provided positionId does not exist";

    // groups will allow running validator based on something such as Create.class etc
    // Class<?>[] groups() default{};
}
