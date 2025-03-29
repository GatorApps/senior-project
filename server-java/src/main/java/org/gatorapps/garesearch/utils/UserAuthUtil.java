package org.gatorapps.garesearch.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

/** UTIL to retrieve UserAuth information
 * eg: opid
 */
@Component
public class UserAuthUtil {

    public String retrieveOpid(HttpServletRequest request) throws Exception {
        if (request.getAttribute("userAuth") == null) {
            throw new Exception("Internal server error");
        }

        ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth");

        if (userAuth.getAuthedUser() == null || userAuth.getAuthedUser().getOpid() == null) {
            throw new Exception("Internal server error");
        }

        return userAuth.getAuthedUser().getOpid();
    }
}
