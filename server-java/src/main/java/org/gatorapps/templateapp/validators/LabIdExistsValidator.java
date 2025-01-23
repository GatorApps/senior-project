package org.gatorapps.templateapp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gatorapps.templateapp.repository.garesearch.LabRepository;
import org.gatorapps.templateapp.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Custom validator function checking if positionId exists in Position **/
@Component
public class LabIdExistsValidator implements ConstraintValidator<LabIdExists, String> {
    @Autowired
    private LabRepository labRepository;

    @Override
    public boolean isValid(String labId, ConstraintValidatorContext context) {
        try {
            return labRepository.existsByLabId(labId);
        } catch (Exception e) {
            return false;
        }
    }
}
