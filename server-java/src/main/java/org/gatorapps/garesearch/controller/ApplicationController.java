package org.gatorapps.garesearch.controller;

import jakarta.validation.constraints.Pattern;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/application")
public class ApplicationController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    ApplicationService applicationService;

    // TODO : test all routes below

    @GetMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(@RequestParam(value = "applicationId", required = true) String applicationId){
        Application application = applicationService.getStudentApplication(applicationId);

        Map<String, Object> payloadResponse = Map.of(
                "application", application);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stuList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications(
            @RequestParam(required=true)
            @Pattern(regexp = "saved|active|inactive", message = "Status must be one of 'saved', 'active', 'inactive'")
            String status ) throws Exception {

        List<Map> foundApplications = applicationService.getStudentApplications(status);

        Map<String, Object> payloadResponse = Map.of(
                "applications", foundApplications);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/application")
    public ResponseEntity<ApiResponse<Void>> submitApplication(
            @RequestParam(value = "positionId", required = true) String positionId,
            @RequestParam(value = "saveApp", required = false) String saveApp) throws Exception {

        applicationService.submitApplication(positionId, saveApp);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
