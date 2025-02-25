package org.gatorapps.garesearch.config;

import org.gatorapps.garesearch.middleware.ValidateOriginInterceptor;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ValidateOriginInterceptor validateOriginInterceptor;
    private final ValidateUserAuthInterceptor validateUserAuthInterceptor;

    public WebConfig(ValidateOriginInterceptor validateOriginInterceptor, ValidateUserAuthInterceptor validateUserAuthInterceptor) {
        this.validateOriginInterceptor = validateOriginInterceptor;
        this.validateUserAuthInterceptor = validateUserAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validateOriginInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(validateUserAuthInterceptor)
                .addPathPatterns("/appApi/garesearch/**");
    }
}
