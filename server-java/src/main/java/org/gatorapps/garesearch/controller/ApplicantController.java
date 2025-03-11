package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.gatorapps.garesearch.exception.FileValidationException;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.File;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.service.ApplicantService;
import org.gatorapps.garesearch.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appApi/garesearch/applicant")
public class ApplicantController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    private ApplicantProfileRepository applicantProfileRepository;

    private final S3Service s3Service;

    public ApplicantController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /*
        follows old logic

        response.payload returns: applicant profile and update endpoint route ?
     */
//    @GetMapping("/profile")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicantProfile(){
//        ApplicantProfile applicant = applicantService.getProfileById();
//
//
//        /* Response is a lot of nested jsons
//            {
//                errCode: '0',
//                payload: {
//                    applicantProfile: {
//                        data: foundProfile,
//                        update: {
//                            endpoint: {
//                                method: "put",
//                                route: "/applicant/profile"
//                            }
//                        }
//                    }
//                }
//            }
//         */
//
//        Map<String, Object> payloadResponse = Map.of(
//                "applicantProfile", Map.of(
//                        "data", applicant,
//                        "update", Map.of(
//                                "endpoint", Map.of(
//                                        "method", "put",
//                                        "route", "/applicant/profile")))
//        );
//
//
//        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    /*
        follows old logic

        no payload
     */
//    @PutMapping("/profile")
//    public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(@RequestBody ApplicantProfile applicantProfile) throws Exception {
//
//        applicantService.updateProfileById(applicantProfile);
//
//        ApiResponse<Void> response = new ApiResponse<>("0");
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    @PostMapping("/resume")
    public ResponseEntity<?> uploadApplicantResume(@Valid HttpServletRequest request, @RequestParam("resume") MultipartFile file) {
        try {
            // Fetch applicant profile
            ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth");
            Optional<ApplicantProfile> applicantProfileOptional = applicantProfileRepository.findByOpid(userAuth.getAuthedUser().getOpid());
            ApplicantProfile applicantProfile = applicantProfileOptional.orElseThrow(() -> new RuntimeException("Applicant profile not found"));

            // Upload file to S3
            File resumeFile = s3Service.uploadFile(file, List.of("pdf"), (long) 5242880, "uploads/", userAuth.getAuthedUser().getOpid(), "resume");

            // Update applicant profile
            applicantProfile.setResumeId(resumeFile.getId());
            applicantProfileRepository.save(applicantProfile);

            ApiResponse<String> response = new ApiResponse<>("0", "{\"resume\": \"" + resumeFile.getId() + "\"}");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FileValidationException e) {
            ErrorResponse<String> response = new ErrorResponse<>("-",  e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Unable to upload file: %s".formatted(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transcript")
    public ResponseEntity<?> uploadApplicantTranscript(HttpServletRequest request, @RequestParam("transcript") MultipartFile file) {
        try {
            // Fetch applicant profile
            ValidateUserAuthInterceptor.UserAuth userAuth = (ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth");
            Optional<ApplicantProfile> applicantProfileOptional = applicantProfileRepository.findByOpid(userAuth.getAuthedUser().getOpid());
            ApplicantProfile applicantProfile = applicantProfileOptional.orElseThrow(() -> new RuntimeException("Applicant profile not found"));

            // Upload file to S3
            File transcriptFile = s3Service.uploadFile(file, List.of("pdf"), (long) 5242880, "uploads/", userAuth.getAuthedUser().getOpid(), "transcript");

            // Update applicant profile
            applicantProfile.setTranscriptId(transcriptFile.getId());
            applicantProfileRepository.save(applicantProfile);

            ApiResponse<String> response = new ApiResponse<>("0", "{\"transcript\": \"" + transcriptFile.getId() + "\"}");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FileValidationException e) {
            ErrorResponse<String> response = new ErrorResponse<>("-",  e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Unable to upload file: %s".formatted(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
