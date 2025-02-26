package org.gatorapps.garesearch.middleware;

import org.gatorapps.garesearch.model.account.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class RequireUserAuthInterceptor implements HandlerInterceptor {

    private final List<List<Integer>> roleRules;

    public RequireUserAuthInterceptor(List<List<Integer>> roleRules) {
        this.roleRules = roleRules;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Retrieve userAuth from request attributes
        Object userAuthObj = request.getAttribute("userAuth");
        if (!(userAuthObj instanceof ValidateUserAuthInterceptor.UserAuth)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Internal server error\"}");
            return false;
        }
        ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) userAuthObj;

        ValidateUserAuthInterceptor.AuthError authError = userAuth.getAuthError();

        // Validate authError object
        if (authError == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Internal server error\"}");
            return false;
        }

        String errCode = authError.getErrCode();
        if (!errCode.equals("0")) {
            int status = authError.getStatus();
            String errMsg = authError.getErrMsg();

            if (HttpStatus.resolve(status) == null || errMsg == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Internal server error\"}");
                return false;
            }

            response.setStatus(status);
            response.getWriter().write(String.format("{\"errCode\": \"%s\", \"errMsg\": \"%s\"}", errCode, errMsg));
            return false;
        }

        // Validate authenticated user
        User authedUser = userAuth.getAuthedUser();
        if (authedUser == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Internal server error\"}");
            return false;
        }

        // Role-based access control
        if (roleRules != null && !roleRules.isEmpty()) {
            List<Integer> userRoles = authedUser.getRoles();

            if (userRoles == null || userRoles.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Insufficient permission\"}");
                return false;
            }

            for (List<Integer> roles : roleRules) {
                boolean failed = false;
                for (Integer role : roles) {
                    if (!userRoles.contains(role)) {
                        failed = true;
                        break;
                    }
                }
                if (!failed) {
                    return true;
                }
            }

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Insufficient permission\"}");
            return false;
        }

        return true;
    }
}
