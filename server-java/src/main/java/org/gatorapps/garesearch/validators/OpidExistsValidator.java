package org.gatorapps.garesearch.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gatorapps.garesearch.repository.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Custom validator function checking if opid exists in User **/
@Component
public class OpidExistsValidator implements ConstraintValidator<OpidExists, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String opid, ConstraintValidatorContext context){
        try {
            return userRepository.existsByOpid(opid);
        } catch (Exception e) {
            return false;
        }
    }
}
