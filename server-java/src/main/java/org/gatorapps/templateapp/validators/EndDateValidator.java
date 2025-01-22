package org.gatorapps.templateapp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gatorapps.templateapp.model.garesearch.BaseApplicationProfileSchema;
import org.gatorapps.templateapp.repository.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;

/** Custom validator function checking if opid exists in User **/
@Component
public class EndDateValidator implements ConstraintValidator<EndDateValid, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context){
        try {
            // Use reflection to get the startDate and endDate fields dynamically
            Field startDateField = value.getClass().getDeclaredField("startDate");
            Field endDateField = value.getClass().getDeclaredField("endDate");

            startDateField.setAccessible(true);
            endDateField.setAccessible(true);

            Date startDate = (Date) startDateField.get(value);
            Date endDate = (Date) endDateField.get(value);

            if (startDate != null && endDate != null) {
                return endDate.after(startDate);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return true; // Return true if the validation passed (e.g., null values handled elsewhere)
    }
}
