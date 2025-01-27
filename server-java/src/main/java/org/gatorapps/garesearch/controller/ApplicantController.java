package org.gatorapps.garesearch.controller;

import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(@Valid @RequestBody ApplicantProfile applicantProfile) throws Exception {

        applicantService.updateProfileById(applicantProfile);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // TODO : the rest of these

    @GetMapping("/applications")
    public ResponseEntity<Map<String, Object>> getStudentApplications(@RequestParam(required=false) String status ){
        // logic done in service
        // applicationService.getStudentApplications()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

    @PostMapping("/application")
    public ResponseEntity<Map<String, Object>> submitApplication(@RequestParam(value = "positionId", required = false) String positionId, @RequestParam(value = "saveApp", required = false) String saveApp){
        // logic done in service
        // applicationService.submitApplication()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }
}
