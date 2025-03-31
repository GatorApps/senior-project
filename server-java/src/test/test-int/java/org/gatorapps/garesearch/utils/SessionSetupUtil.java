package org.gatorapps.garesearch.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class SessionSetupUtil {
    public static MockHttpServletRequestBuilder performRequest(MockMvc mockMvc, String route, String authHeaderName, String authHeaderVal, String headerName, String headerValue) throws Exception {
        return MockMvcRequestBuilders.get(route)
                .header(authHeaderName, authHeaderVal)
                .header(headerName, headerValue)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
