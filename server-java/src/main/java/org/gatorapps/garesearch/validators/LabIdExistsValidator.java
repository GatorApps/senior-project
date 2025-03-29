package org.gatorapps.garesearch.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Custom validator function checking if labId exists in Lab **/
@Component
public class LabIdExistsValidator implements ConstraintValidator<LabIdExists, String> {

    @Autowired
    private LabRepository labRepository;
    @Override
    public boolean isValid(String labId, ConstraintValidatorContext context) {
        try {
            return labRepository.existsById(labId);
        } catch (Exception e) {
            return false;
        }
    }
}