package org.gatorapps.garesearch.middleware;

import org.gatorapps.garesearch.model.global.App;
import org.gatorapps.garesearch.repository.global.AppRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ValidateOriginInterceptor implements HandlerInterceptor {

    private final AppRepository appRepository;

    public ValidateOriginInterceptor(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestingApp = request.getHeader("GATORAPPS_APP");
        if (requestingApp == null || requestingApp.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Missing requesting app\"}");
            return false;
        }

        // Fetch app details from MongoDB
        Optional<App> foundApp = appRepository.findByName(requestingApp);

        if (foundApp.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Requesting app does not exist\"}");
            return false;
        }

        // Validate the origin header
//        String origin = request.getHeader("Origin");
//        if (origin != null && !foundApp.get().getOrigins().contains(origin)) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("{\"errCode\": \"-\", \"errMsg\": \"Unauthorized or mismatched origin\"}");
//            return false;
//        }

        // Store foundApp in request attributes so controllers can access it
        request.setAttribute("reqApp", foundApp.get());

        return true; // Proceed with request
    }
}
