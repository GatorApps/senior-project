package org.gatorapps.garesearch.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class WebConfigTests {
    String[] requiredInts = {
            "ValidateOriginInterceptor", "ValidateUserAuthInterceptor", "HttpMethodInterceptor", "RequireUserAuthInterceptor"
    };

    String[] requiredInts_applicant = {
            "ValidateOriginInterceptor", "ValidateUserAuthInterceptor", "HttpMethodInterceptor", "RequireUserAuthInterceptor", "RequireApplicantProfileInterceptor"
    };

    String[][] routes = {
            { "GET", "/appApi/garesearch/application" },
            { "GET", "/appApi/garesearch/application/studentList" },
            { "GET", "/appApi/garesearch/application/studentList" },
            { "GET", "/appApi/garesearch/application/alreadyApplied" },
            { "POST", "/appApi/garesearch/application" },
            { "GET", "/appApi/garesearch/application/application" },
            { "GET", "/appApi/garesearch/application/applicationManagement" },
            { "GET", "/appApi/garesearch/lab/labsList" },
            { "GET", "/appApi/garesearch/lab/profileEditor" },
            { "GET", "/appApi/garesearch/posting/postingsList" },
            { "GET", "/appApi/garesearch/posting/postingManagement" },
            { "GET", "/appApi/garesearch/posting/postingEditor" },
            { "POST", "/appApi/garesearch/lab/profileEditor" },
            { "POST", "/appApi/garesearch/posting/postingEditor" },
            { "PUT", "/appApi/garesearch/application/applicationStatus" },
            { "PUT", "/appApi/garesearch/lab/profileEditor" },
            { "PUT", "/appApi/garesearch/posting/postingEditor" },
            { "PUT", "/appApi/garesearch/posting/postingStatus" },

            // RequireApplicantProfileInterceptor
            { "POST", "/appApi/garesearch/applicant/resume" },
            { "POST", "/appApi/garesearch/applicant/transcript" },
            { "GET", "/appApi/garesearch/applicant/resumeMetadata" },
            { "GET", "/appApi/garesearch/applicant/transcriptMetadata" },
    };


    @Autowired
    private RequestMappingHandlerMapping mapping;


    ArrayList<String> printOut(List<HandlerInterceptor> interceptors){
        ArrayList<String> intNames = new ArrayList<>();
        for (int i = 0; i < interceptors.size(); i++) {
            intNames.add(interceptors.get(i).getClass().getSimpleName());
            System.out.println(i + ": " + interceptors.get(i).getClass().getSimpleName());

            if (interceptors.get(i).getClass().getSimpleName().equals("HttpMethodInterceptor")) {
                try {
                    Field delegateField = interceptors.get(i).getClass().getDeclaredField("delegate");
                    delegateField.setAccessible(true);
                    Object delegate = delegateField.get(interceptors.get(i));

                    intNames.add(delegate.getClass().getSimpleName());
                    System.out.println("    -> Wrapped interceptor: " + delegate.getClass().getSimpleName());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return intNames;
    }

    @Test
    public void routesInterceptors() throws Exception {
        int i = 0;
        String[] requiredInterceptors = requiredInts;
        for (String[] route : routes){
            System.out.println("---------" + route[0] + "---------" + route[1] + "---------");
            System.out.println("----------------------------------------------------------------------");
            MockHttpServletRequest request = new MockHttpServletRequest(route[0], route[1]);

            HandlerExecutionChain chain = mapping.getHandler(request);
            assert chain != null;

            List<HandlerInterceptor> interceptors = chain.getInterceptorList();

            ArrayList<String>  intNames = printOut(interceptors);


            if (i == 18){
                requiredInterceptors = requiredInts_applicant;
            }

            for (String expectedInt : requiredInterceptors) {
                boolean interceptorFound = intNames.stream().anyMatch(name -> name.equals(expectedInt)); ;

                assertTrue(interceptorFound, "Interceptor " + expectedInt + " not found for route " + route[1]);
            }

            System.out.println("----------------------------------------------------------------------");
            i++;
        }
    }
}
