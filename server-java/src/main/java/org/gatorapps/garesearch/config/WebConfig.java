package org.gatorapps.garesearch.config;

import org.gatorapps.garesearch.middleware.ValidateOriginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ValidateOriginInterceptor validateOriginInterceptor;

    public WebConfig(ValidateOriginInterceptor validateOriginInterceptor) {
        this.validateOriginInterceptor = validateOriginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validateOriginInterceptor)
                .addPathPatterns("/appApi/garesearch/**"); // Apply to all /api routes
    }
}
