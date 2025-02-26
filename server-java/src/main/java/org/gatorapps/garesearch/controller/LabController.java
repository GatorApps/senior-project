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

    /*
        follows old logic

        response.payload returns: lab profile

        will likely need update to join with positions or something
     */
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabProfile(){

        // Lab lab = labService.getProfile();
        Lab lab = new Lab();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "labProfile", lab);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createLabProfile(){
        // Lab lab = labService.createProfile();
        Lab lab = new Lab();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "labProfile", lab);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateLabProfile(){
        // Lab lab = labService.updateProfile();
        Lab lab = new Lab();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "labProfile", lab);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
