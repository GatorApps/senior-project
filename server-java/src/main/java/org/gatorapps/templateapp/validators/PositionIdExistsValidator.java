package org.gatorapps.templateapp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gatorapps.templateapp.repository.account.UserRepository;
import org.gatorapps.templateapp.repository.garesearch.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Custom validator function checking if positionId exists in Position **/
@Component
public class PositionIdExistsValidator implements ConstraintValidator<PositionIdExists, String> {
    @Autowired
    private PositionRepository positionRepository;

    @Override
    public boolean isValid(String positionId, ConstraintValidatorContext context) {
        try {
            return positionRepository.existsByPositionId(positionId);
        } catch (Exception e) {
            return false;
        }
    }
}
