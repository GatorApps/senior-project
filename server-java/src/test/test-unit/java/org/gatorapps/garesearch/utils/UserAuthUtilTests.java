package org.gatorapps.garesearch.utils;


import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.account.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

public class UserAuthUtilTests {
    private UserAuthUtil userAuthUtil;
    private MockHttpServletRequest request;

    private String opid = "abc0c01ab87e195493ae9c10";
    ValidateUserAuthInterceptor.AuthError authError = new ValidateUserAuthInterceptor.AuthError("0");

    ValidateUserAuthInterceptor.AuthError authError_error = new ValidateUserAuthInterceptor.AuthError(403, "-", "Invalid user opid");

    @BeforeEach
    void setUp(){
        userAuthUtil = new UserAuthUtil();
        request = new MockHttpServletRequest();
    }

    @Test
    void retrieveOpid_Valid() throws Exception {
        User user = new User();
        user.setOpid(opid);
        ValidateUserAuthInterceptor.UserAuth userAuth = new ValidateUserAuthInterceptor.UserAuth(user, authError);

        request.setAttribute("userAuth", userAuth);

        String opid = userAuthUtil.retrieveOpid(request);
        assertEquals(opid, opid);
    }

    @Test
    void retrieveOpid_Null() throws Exception {
        ValidateUserAuthInterceptor.UserAuth userAuth = new ValidateUserAuthInterceptor.UserAuth(null, authError_error);

        request.setAttribute("userAuth", userAuth);


        assertThrows(Exception.class, ()->{
            userAuthUtil.retrieveOpid(request);
        });
    }
}






