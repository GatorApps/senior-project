package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/posting")
public class PositionController {
    @Autowired
    PositionService positionService;


    /*
        response.payload returns: list of positions
     */
    @GetMapping("/searchList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(@RequestParam(value = "searchParams") String searchParams) throws Exception {
        List<Map> positions = positionService.getSearchResults(searchParams);

        Map<String, Object> payloadResponse = Map.of(
                "positions", positions);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        response.payload returns: list of positions. similar to above, but only returns position id , name
     */
    @GetMapping("/searchIndexer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchIndexerResults(@RequestParam(value = "searchParams") String searchParams) throws Exception {
        List<Map> positions = positionService.getSearchIndexerResults(searchParams);

        Map<String, Object> payloadResponse = Map.of(
                "positions", positions);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    /*
        follows old logic

        response.payload returns: position by positionId

        will likely need update to join with Lab or something
     */
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPosting(){
        // Position position = positionService.getPosting();
        Position position = new Position();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "positionPosting", position);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPositionPosting(){
        // Position position = positionService.createPosting();
        Position position = new Position();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "positionPosting", position);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePositionPosting(){
        // Position position = positionService.updatePosting();
        Position position = new Position();

        // Define Payload Structure first
        Map<String, Object> payloadResponse = Map.of(
                "positionPosting", position);

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}

