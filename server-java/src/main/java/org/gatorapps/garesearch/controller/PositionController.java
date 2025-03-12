package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/appApi/garesearch/posting")
public class PositionController {
    @Autowired
    PositionService positionService;

    /*
        response.payload returns: position by positionId
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(@RequestParam(value = "positionId", required = true) String positionId) throws Exception {

        Map position = positionService.getPublicPosting(positionId);

        Map<String, Object> payloadResponse = Map.of(
                "position", position);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        response.payload returns: list of positions
     */
    @GetMapping("/searchList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(@RequestParam(value = "q") String searchParams) throws Exception {
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchIndexerResults(@RequestParam(value = "q") String searchParams) throws Exception {
        List<Map> positions = positionService.getSearchIndexerResults(searchParams);

        Map<String, Object> payloadResponse = Map.of(
                "positions", positions);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/applicationQuestions")
    public ResponseEntity<?> getApplicationQuestions(@Valid HttpServletRequest request, @RequestParam(value = "positionId") String positionId) {
        Optional<Position> positionOptional = positionService.getPosting(positionId);
        if (positionOptional.isEmpty()) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Position not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Position position = positionOptional.get();

        Map<String, Object> responsePayload = Map.of(
                "position", Map.of(
                        "applicationQuestions", position.getApplicationQuestions()
                )
        );
        ApiResponse<Object> response = new ApiResponse<>("0", responsePayload);
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

    /*
        may be tweaked in future, but is working. created to set up rawDescription field
            (description --- remove html tags ---> rawDescription)
     */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> createPositionPosting(@RequestBody Position position) throws Exception {
        positionService.createPosting(position);
        //Position position = new Position();

        // Predefined ApiResponse class : { errCode: xyz, payload: xyz}
        ApiResponse<Void> response = new ApiResponse<Void>("0");

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

