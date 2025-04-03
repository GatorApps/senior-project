package org.gatorapps.garesearch.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gatorapps.garesearch.middleware.RequireApplicantProfileInterceptor;
import org.gatorapps.garesearch.middleware.RequireUserAuthInterceptor;
import org.gatorapps.garesearch.middleware.ValidateOriginInterceptor;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ValidateOriginInterceptor validateOriginInterceptor;
    private final ValidateUserAuthInterceptor validateUserAuthInterceptor;
    private final RequireApplicantProfileInterceptor requireApplicantProfileInterceptor;

    public WebConfig(ValidateOriginInterceptor validateOriginInterceptor, ValidateUserAuthInterceptor validateUserAuthInterceptor,
                     RequireUserAuthInterceptor requireUserAuthInterceptor, RequireApplicantProfileInterceptor requireApplicantProfileInterceptor) {
        this.validateOriginInterceptor = validateOriginInterceptor;
        this.validateUserAuthInterceptor = validateUserAuthInterceptor;
        this.requireApplicantProfileInterceptor = requireApplicantProfileInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validateOriginInterceptor)
                .addPathPatterns("/appApi/garesearch/**")
                .order(1);
        registry.addInterceptor(validateUserAuthInterceptor)
                .addPathPatterns("/appApi/garesearch/**")
                .order(1);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("GET"), new RequireUserAuthInterceptor(List.of(List.of(100001)))))
                .addPathPatterns("/appApi/garesearch/**")
                .excludePathPatterns("/appApi/garesearch/renderClient/appInfo")
                .order(2);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("GET"), new RequireUserAuthInterceptor(List.of(List.of(500201)))))

                .addPathPatterns("/appApi/garesearch/application",
                        "/appApi/garesearch/application/studentList",
                        "/appApi/garesearch/application/alreadyApplied")
                .order(3);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("POST"), new RequireUserAuthInterceptor(List.of(List.of(500201)))))
                .addPathPatterns("/appApi/garesearch/application")
                .order(3);

        registry.addInterceptor(new RequireUserAuthInterceptor(List.of(List.of(500201))))
                .addPathPatterns("/appApi/garesearch/applicant/**")
                .order(3);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("GET"), new RequireUserAuthInterceptor(List.of(List.of(500301)))))
                .addPathPatterns("/appApi/garesearch/application/application",
                        "/appApi/garesearch/application/applicationManagement",
                        "/appApi/garesearch/lab/labsList",
                        "/appApi/garesearch/lab/profileEditor",
                        "/appApi/garesearch/posting/postingsList",
                        "/appApi/garesearch/posting/postingManagement",
                        "/appApi/garesearch/posting/postingEditor")
                .order(3);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("POST"), new RequireUserAuthInterceptor(List.of(List.of(500301)))))
                .addPathPatterns("/appApi/garesearch/lab/profileEditor",
                        "/appApi/garesearch/posting/postingEditor")
                .order(3);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("PUT"), new RequireUserAuthInterceptor(List.of(List.of(500301)))))
                .addPathPatterns("/appApi/garesearch/application/applicationStatus",
                        "/appApi/garesearch/lab/profileEditor",
                        "/appApi/garesearch/posting/postingEditor",
                        "/appApi/garesearch/posting/postingStatus")
                .order(3);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("POST"), requireApplicantProfileInterceptor))
                .addPathPatterns("/appApi/garesearch/applicant/resume", "/appApi/garesearch/applicant/transcript")
                .order(4);

        registry.addInterceptor(new HttpMethodInterceptor(Set.of("GET"), requireApplicantProfileInterceptor))
                .addPathPatterns("/appApi/garesearch/applicant/resumeMetadata", "/appApi/garesearch/applicant/transcriptMetadata")
                .order(4);
    }

    private class HttpMethodInterceptor implements HandlerInterceptor {
        private final Set<String> allowedMethods;
        private final HandlerInterceptor delegate;

        public HttpMethodInterceptor(Set<String> allowedMethods, HandlerInterceptor delegate) {
            this.allowedMethods = allowedMethods;
            this.delegate = delegate;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (allowedMethods.contains(request.getMethod())) {
                return delegate.preHandle(request, response, handler);
            }
            return true; // Skip interceptor for other methods
        }
    }
}