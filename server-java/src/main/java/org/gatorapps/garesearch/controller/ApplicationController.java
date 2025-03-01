package org.gatorapps.garesearch.controller;


import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
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

    /*
        response.payload returns single application by ID.

        logic will need to be updated during S3 integration most likely
     */
    @GetMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(@RequestParam(value = "applicationId", required = true) String applicationId) throws Exception{
        Map application = applicationService.getStudentApplication(applicationId);

        Map<String, Object> payloadResponse = Map.of(
                "application", application);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        response.payload returns: 2 lists of applications
     */
    @GetMapping("/studentList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications() throws Exception {
        List<Map> foundApplications = applicationService.getStudentApplications();

        List<Map> submittedApps = foundApplications.stream()
                .filter(app -> "Submitted".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();
        List<Map> archivedApps = foundApplications.stream()
                .filter(app -> "Archived".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("submissionTimeStamp")))
                .toList();


        Map<String, Object> payloadResponse = Map.of(
                "applications", Map.of(
                        "activeApplications", submittedApps,
                        "archivedApplications", archivedApps
                ));


        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: tests
    /*
        follows old logic

        no payload
     */
    @PostMapping("/application")
    public ResponseEntity<ApiResponse<Void>> submitApplication(
            @RequestParam(value = "positionId", required = true) String positionId,
            @RequestParam(value = "saveApp", required = false) String saveApp) throws Exception {

        applicationService.submitApplication(positionId, saveApp);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
