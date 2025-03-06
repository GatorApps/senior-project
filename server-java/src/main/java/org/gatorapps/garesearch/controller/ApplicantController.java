package org.gatorapps.garesearch.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.ApplicationService;
import org.gatorapps.garesearch.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/applicant")
public class ApplicantController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    ApplicationService applicationService;

    private final S3Service s3Service;

    public ApplicantController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

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


    @PostMapping("/resume")
    public ResponseEntity<?> uploadApplicantResume(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("application/pdf")) {
                ErrorResponse<Void> response = new ErrorResponse<>("-", "Invalid file type. Only PDF files are allowed");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String fileUrl = s3Service.uploadFile(file, List.of("pdf"), null);
            ApiResponse<String> response = new ApiResponse<>("0", "{\"fileUrl\": \"" + fileUrl + "\"}");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Unable to upload file: %s".formatted(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
