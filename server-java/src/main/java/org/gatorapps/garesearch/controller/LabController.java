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
        updates existing or creates new lab
    */
    @PostMapping("/profileEditor")
    public ResponseEntity<ApiResponse<Void>> saveLabProfile(@Valid HttpServletRequest request, @Valid @RequestBody Lab lab) throws Exception {
        labService.saveProfile(userAuthUtil.retrieveOpid(request), lab);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
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
}
