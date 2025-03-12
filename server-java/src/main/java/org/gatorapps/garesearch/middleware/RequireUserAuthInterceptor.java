package org.gatorapps.garesearch.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.dto.ErrorResponse;
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
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson's ObjectMapper

    public RequireUserAuthInterceptor(List<List<Integer>> roleRules) {
        this.roleRules = roleRules;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Retrieve userAuth from request attributes
        Object userAuthObj = request.getAttribute("userAuth");
        if (!(userAuthObj instanceof ValidateUserAuthInterceptor.UserAuth)) {
            return sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "-", "Internal server error");
        }
        ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) userAuthObj;

        ValidateUserAuthInterceptor.AuthError authError = userAuth.getAuthError();

        // Validate authError object
        if (authError == null) {
            return sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "-", "Internal server error");
        }

        String errCode = authError.getErrCode();
        if (!errCode.equals("0")) {
            int status = authError.getStatus();
            String errMsg = authError.getErrMsg();

            if (HttpStatus.resolve(status) == null || errMsg == null) {
                return sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "-", "Internal server error");
            }

            return sendErrorResponse(response, status, errCode, errMsg);
        }

        // Validate authenticated user
        User authedUser = userAuth.getAuthedUser();
        if (authedUser == null) {
            return sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "-", "Internal server error");
        }

        // Role-based access control
        if (roleRules != null && !roleRules.isEmpty()) {
            List<Integer> userRoles = authedUser.getRoles();

            if (userRoles == null || userRoles.isEmpty()) {
                return sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "-", "Insufficient permission");
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

            return sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "-", "Insufficient permission");
        }

        return true;
    }

    private boolean sendErrorResponse(HttpServletResponse response, int status, String errCode, String errMsg) throws Exception {
        ErrorResponse<Object> errorResponse = new ErrorResponse<>(errCode, errMsg);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        return false;
    }
}
