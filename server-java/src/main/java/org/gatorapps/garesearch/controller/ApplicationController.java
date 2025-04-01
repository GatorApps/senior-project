package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
import org.gatorapps.garesearch.utils.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/application")
public class ApplicationController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    UserAuthUtil userAuthUtil;

    /*
        response.payload returns single application by ID.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(HttpServletRequest request, @RequestParam(value = "applicationId", required = true) String applicationId) throws Exception {
        Application application = applicationService.getStudentApplication(userAuthUtil.retrieveOpid(request), applicationId);

        Map<String, Object> payloadResponse = Map.of(
                "application", application);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        response.payload returns: 3 lists of applications
     */
    @GetMapping("/studentList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications(HttpServletRequest request) throws Exception {
        List<Map> foundApplications = applicationService.getStudentApplications(userAuthUtil.retrieveOpid(request));

        List<Map> submittedApps = foundApplications.stream()
                .filter(app -> "Submitted".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();
        List<Map> movingApps = foundApplications.stream()
                .filter(app -> "Moving Forward".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();
        List<Map> archivedApps = foundApplications.stream()
                .filter(app -> "Archived".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();


        Map<String, Object> payloadResponse = Map.of(
                "applications", Map.of(
                        "activeApplications", submittedApps,
                        "movingApplications", movingApps,
                        "archivedApplications", archivedApps
                ));


        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        submit application for a particular position
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> submitApplication(
            @Valid HttpServletRequest request,
            @RequestParam(value = "positionId", required = true) String positionId,
            @RequestBody(required = true) Map<String, Object> requestBody) throws Exception {
        // Retrieve authedUser from request attributes
        User authedUser = ((ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth")).getAuthedUser();

        // Retrieve application json from request body
        Map<String, Object> application = (Map<String, Object>) requestBody.get("application");

        // Submit application
        applicationService.submitApplication(authedUser.getOpid(), positionId, application);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/alreadyApplied")
    public ResponseEntity<ApiResponse<Map<String, Object>>> alreadyApplied(HttpServletRequest request, @RequestParam(value = "positionId", required = true) String positionId) throws Exception {
        boolean alreadyApplied = applicationService.alreadyApplied(userAuthUtil.retrieveOpid(request), positionId);

        Map<String, Object> payloadResponse = Map.of(
                "alreadyApplied", alreadyApplied);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     Exclusively Faculty Routes
     */

    /*
        response.payload returns: application by applicationId
     */
    @GetMapping("/application")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplication(@Valid HttpServletRequest request,
                                                                     @RequestParam(value = "labId") String labId,
                                                                     @RequestParam(value = "applicationId") String applicationId) throws Exception {
        Application application = applicationService.getApplication(userAuthUtil.retrieveOpid(request), labId, applicationId);

        Map<String, Object> payloadResponse = Map.of(
                "application", application);


        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        response.payload returns: 3 lists of applications students have submitted for particular position
     */
    @GetMapping("/applicationManagement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationList(HttpServletRequest request, @RequestParam (value="positionId") String positionId) throws Exception {
        List<Map> foundApplications = applicationService.getApplicationList(userAuthUtil.retrieveOpid(request), positionId);

        List<Map> submittedApps = foundApplications.stream()
                .filter(app -> "Submitted".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();
        List<Map> movingApps = foundApplications.stream()
                .filter(app -> "Moving Forward".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();
        List<Map> archivedApps = foundApplications.stream()
                .filter(app -> "Archived".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();


        Map<String, Object> payloadResponse = Map.of(
                "applications", Map.of(
                        "activeApplications", submittedApps,
                        "movingApplications", movingApps,
                        "archivedApplications", archivedApps
                ));


        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        updates application status
     */
    @PutMapping("/applicationStatus")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(@Valid HttpServletRequest request,
                                                                     @RequestParam(value = "labId") String labId,
                                                                     @RequestParam(value = "applicationId") String applicationId,
                                                                     @RequestParam(value = "status") @Pattern(regexp = "submitted|archived|moving forward", message = "Application status must be one of 'submitted', 'archived', or 'moving forward'") String status) throws Exception {
        applicationService.updateStatus(userAuthUtil.retrieveOpid(request), labId, applicationId, status);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
