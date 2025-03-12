package org.gatorapps.garesearch.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

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
        ApplicantProfile applicantProfile;
        Optional<ApplicantProfile> applicantProfileOptional = applicantProfileRepository.findByOpid(authedUser.getOpid());
        if (applicantProfileOptional.isEmpty()) {
            applicantProfile = new ApplicantProfile();
            applicantProfile.setOpid(authedUser.getOpid());
            applicantProfileRepository.save(applicantProfile);
        } else {
            applicantProfile = applicantProfileOptional.get();
        }

        // Save applicant profile to request attributes
        request.setAttribute("applicantProfile", applicantProfile);

        return true;
    }
}