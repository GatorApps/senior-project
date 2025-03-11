package org.gatorapps.garesearch.config;

import org.gatorapps.garesearch.middleware.RequireApplicantProfileInterceptor;
import org.gatorapps.garesearch.middleware.RequireUserAuthInterceptor;
import org.gatorapps.garesearch.middleware.ValidateOriginInterceptor;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

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
                .addPathPatterns("/**");
        registry.addInterceptor(validateUserAuthInterceptor)
                .addPathPatterns("/appApi/garesearch/**");

        registry.addInterceptor(new org.gatorapps.garesearch.middleware.RequireUserAuthInterceptor(List.of(List.of(100001))))
                .addPathPatterns("/appApi/garesearch/posting/searchList");

        registry.addInterceptor(new org.gatorapps.garesearch.middleware.RequireUserAuthInterceptor(List.of(List.of(500201))))
                .addPathPatterns("/appApi/garesearch/applicant/**", "/appApi/garesearch/application/studentList");

        registry.addInterceptor(requireApplicantProfileInterceptor)
                .addPathPatterns("/appApi/garesearch/applicant/resume", "/appApi/garesearch/applicant/transcript");
    }
}
