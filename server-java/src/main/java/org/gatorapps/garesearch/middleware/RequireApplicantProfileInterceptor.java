package org.gatorapps.garesearch.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequireApplicantProfileInterceptor implements HandlerInterceptor {

    private final ApplicantProfileRepository applicantProfileRepository;

    public RequireApplicantProfileInterceptor(ApplicantProfileRepository applicantProfileRepository) {
        this.applicantProfileRepository = applicantProfileRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Retrieve userAuth from request attributes
        User authedUser = ((ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth")).getAuthedUser();

        // Create applicant profile if not already exists
        if (applicantProfileRepository.findByOpid(authedUser.getOpid()).isEmpty()) {
            ApplicantProfile newApplicantProfile = new ApplicantProfile();
            newApplicantProfile.setOpid(authedUser.getOpid());
            applicantProfileRepository.save(newApplicantProfile);
        }
        return true;
    }
}