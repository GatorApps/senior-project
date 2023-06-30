package org.gatorapps.garesearch.controller;

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

    // TODO : all of these

    @GetMapping("/publicProfile")
    public ResponseEntity<Map<String, Object>> getLabPublicProfile(){
        // logic done in service
        // labService.getPublicProfile()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }


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
