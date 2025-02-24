package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/position")
public class PositionController {
    @Autowired
    PositionService positionService;


    @GetMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(@RequestParam(value = "positionId", required = true) String positionId){

        Position position = positionService.getPublicPosting(positionId);

        Map<String, Object> payloadResponse = Map.of(
                "positionPublicPosting", position);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO : all of these below

    @GetMapping("/posting")
    public ResponseEntity<Map<String, Object>> getPositionPosting(){
        // logic done in service
        // positionService.getPosting()


        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> createPositionPosting(){
        // logic done in service
        // positionService.createPosting()

        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updatePositionPosting(){
        // logic done in service
        // positionService.updatePosting()


        Map<String, Object> responsePayload = Map.of(
                "errCode", "0",
                "payload", Map.of()
        );

        return new ResponseEntity<>(responsePayload, HttpStatus.OK);
    }


}

