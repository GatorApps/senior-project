package org.gatorapps.garesearch.controller;

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


    /*
        follows old logic

        response.payload returns: applicant profile and update endpoint route ?
     */
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
                                        "route", "/applicant/profile")))
        );


        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        follows old logic

        no payload
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(@RequestBody ApplicantProfile applicantProfile) throws Exception {

        applicantService.updateProfileById(applicantProfile);

        ApiResponse<Void> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
