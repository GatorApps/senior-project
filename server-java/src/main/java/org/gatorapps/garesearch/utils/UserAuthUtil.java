package org.gatorapps.garesearch.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

/** UTIL to retrieve UserAuth information
 * eg: opid
 */
@Component
public class UserAuthUtil {

    public String retrieveOpid(HttpServletRequest request) throws ConstraintViolationException {
        ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth");
        return userAuth.getAuthedUser().getOpid();
    }
}
