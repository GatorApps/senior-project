package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.repository.account.UserRepository;
import org.gatorapps.garesearch.service.PositionService;
import org.gatorapps.garesearch.utils.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/test")
public class test {

    @Autowired
    UserAuthUtil userAuthUtil;

    @Autowired
    UserRepository userRepository;


    /*
        response.payload returns: position by positionId
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> testFunc() throws Exception {

        boolean y = userRepository.existsByOpid("127ad6f9-a0ff-4e3f-927f-a70b64c542e0");

        Map<String, Object> payloadResponse = Map.of(
                "exists", y);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

