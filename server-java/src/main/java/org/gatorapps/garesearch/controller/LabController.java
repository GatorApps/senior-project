package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/lab")
public class LabController {
    @Autowired
    LabService labService;


    @GetMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabPublicProfile(@RequestParam(value = "labId", required = true) String labId){
        Lab lab = labService.getPublicProfile(labId);

        Map<String, Object> payloadResponse = Map.of(
                "labPublicProfile", lab);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO : all of these below

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getLabProfile(){
        // logic done in service
        // labService.getProfile()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }


    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> createLabProfile(){
        // logic done in service
        // labService.createProfile()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }


    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateLabProfile(){
        // logic done in service
        // labService.updateProfile()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }


}
