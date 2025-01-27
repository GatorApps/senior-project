package org.gatorapps.garesearch.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/applicant")
public class ApplicantController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    ApplicationService applicationService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicantProfile(){
        ApplicantProfile applicant = applicantService.getProfileById();


        /* Response is a lot of nested jsons
            {
                errCode: '0',
                payload: {
                    applicantProfile: {
                        data: foundProfile,
                        update: {
                            endpoint: {
                                method: "put",
                                route: "/applicant/profile"
                            }
                        }
                    }
                }
            }
         */

        Map<String, Object> payloadResponse = Map.of(
                "applicantProfile", Map.of(
                        "data", applicant,
                        "update", Map.of(
                                "endpoint", Map.of(
                                        "method", "put",
                                        "route", "applicant/profile")))
        );


        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO : test all routes below

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(@Valid @RequestBody ApplicantProfile applicantProfile) throws Exception {

        applicantService.updateProfileById(applicantProfile);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/applications")
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
