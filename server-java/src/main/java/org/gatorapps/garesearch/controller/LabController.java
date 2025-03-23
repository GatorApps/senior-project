package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.service.LabService;
import org.gatorapps.garesearch.utils.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/lab")
public class LabController {
    @Autowired
    LabService labService;

    @Autowired
    UserAuthUtil userAuthUtil;

    /*
        response.payload returns: lab profile by labId
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabPublicProfile(@RequestParam(value = "labId", required = true) String labId) throws Exception {
        Map lab = labService.getPublicProfile(labId);

        Map<String, Object> payloadResponse = Map.of(
                "lab", lab);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     Exclusively Faculty Routes
     */

    /*
        response.payload returns: list of labs a faculty is part of
     */
    @GetMapping("/labsList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabsNameList(HttpServletRequest request) throws Exception {
        List<Map> labs = labService.getLabNames(userAuthUtil.retrieveOpid(request));

        Map<String, Object> payloadResponse = Map.of(
                "labs", labs);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        response.payload returns: retrieve position to edit
     */
    @GetMapping("/profileEditor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabProfile(@Valid HttpServletRequest request, @RequestParam(value="labId") String labId) throws Exception {

        Lab lab = labService.getProfile(userAuthUtil.retrieveOpid(request), labId);

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "lab", lab);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        creates new lab
    */
    @PostMapping("/profileEditor")
    public ResponseEntity<ApiResponse<Void>> createLabProfile(@Valid HttpServletRequest request, @Valid @RequestBody Lab lab) throws Exception {
        labService.createProfile(userAuthUtil.retrieveOpid(request), lab);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        updates existing lab
    */
    @PutMapping("/profileEditor")
    public ResponseEntity<ApiResponse<Void>> updateLabProfile(@Valid HttpServletRequest request, @RequestBody Lab lab) throws Exception {
        labService.updateProfile(userAuthUtil.retrieveOpid(request), lab);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
