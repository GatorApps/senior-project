package org.gatorapps.templateapp.controller;

import jakarta.validation.Valid;
import org.gatorapps.templateapp.model.garesearch.ApplicantProfile;
import org.gatorapps.templateapp.service.ApplicantService;
import org.gatorapps.templateapp.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appApi/templateapp/applicant")
public class ApplicantController {
    @Autowired
    ApplicantService applicantService;

    @Autowired
    ApplicationService applicationService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getApplicantProfile(){
        // logic done in service
        // applicantService.getProfileById()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateApplicantProfile(@Valid @RequestBody ApplicantProfile applicantProfile){
        // logic done in service
        // applicantService.updateProfileById()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

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
