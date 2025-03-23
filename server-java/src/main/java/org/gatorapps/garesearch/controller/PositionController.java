package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.bson.types.ObjectId;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.gatorapps.garesearch.service.PositionService;
import org.gatorapps.garesearch.utils.UserAuthUtil;
import org.gatorapps.garesearch.validators.LabIdExists;
import org.gatorapps.garesearch.validators.PositionIdExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/appApi/garesearch/posting")
public class PositionController {
    @Autowired
    PositionService positionService;

    @Autowired
    UserAuthUtil userAuthUtil;


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

    @GetMapping("/supplementalQuestions")
    public ResponseEntity<?> getSupplementalQuestions(@RequestParam(value = "positionId") String positionId) throws Exception {
        Position position = positionService.getPosting(positionId);

        Map<String, Object> responsePayload = new HashMap<>();
        Map<String, Object> positionMap = new HashMap<>();

        positionMap.put("applicationQuestions", position.getSupplementalQuestions());
        responsePayload.put("position", positionMap);

        ApiResponse<Object> response = new ApiResponse<>("0", responsePayload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     Exclusively Faculty Routes
     */

    /*
        response.payload returns: list of positions a faculty has access to
     */
    @GetMapping("/postingsList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionsNamesList(HttpServletRequest request) throws Exception {
        List<Map> positions = positionService.getPostingNames(userAuthUtil.retrieveOpid(request));

        Map<String, Object> payloadResponse = Map.of(
                "positions", positions);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        response.payload returns: list of positions for faculty
     */
    @GetMapping("/postingManagement")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFacultyPostingsList(@Valid HttpServletRequest request) throws Exception {
        List<Map> positions = positionService.getPostingsList(userAuthUtil.retrieveOpid(request));

        List<Map> openPositions = positions.stream()
                .filter(app -> "open".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("postedTimeStamp")))
                .toList();

        List<Map> closedPositions = positions.stream()
                .filter(app -> "closed".equalsIgnoreCase((String) app.get("status")))
                .sorted(Comparator.comparing(app -> (Date) app.get("postedTimeStamp")))
                .toList();

        Map<String, Object> payloadResponse = Map.of(
                "postingsList", Map.of(
                        "openPositions", openPositions,
                        "closedPositions", closedPositions
                ));
        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        response.payload returns: retrieve position to edit
     */
    @GetMapping("/postingEditor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPosting(@Valid HttpServletRequest request, @RequestParam(value = "positionId")String positionId) throws Exception {
        Position position = positionService.getPosting(userAuthUtil.retrieveOpid(request), positionId);

        Map<String, Object> payloadResponse = Map.of(
                "position", position);

        ApiResponse<Map<String, Object>> response = new ApiResponse<Map<String, Object>>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        creates new posting
     */
    @PostMapping("/postingEditor")
    public ResponseEntity<ApiResponse<Void>> createPositionPosting(@Valid HttpServletRequest request, @Valid @RequestBody Position position) throws Exception {
        positionService.createPosting(userAuthUtil.retrieveOpid(request), position);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        updates existing posting
     */
    @PutMapping("/postingEditor")
    public ResponseEntity<ApiResponse<Void>> updatePositionPosting(@Valid HttpServletRequest request, @RequestBody Position position) throws Exception {
        positionService.updatePosting(userAuthUtil.retrieveOpid(request), position);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /*
        updates posting status only
     */
    @PutMapping("/postingStatus")
    public ResponseEntity<ApiResponse<Void>> updatePositionStatus(@Valid HttpServletRequest request,
                                                                  @RequestParam(value = "positionId") String positionId,
                                                                  @RequestParam(value = "status") @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'") String status) throws Exception {
        positionService.updateStatus(userAuthUtil.retrieveOpid(request), positionId, status);

        ApiResponse<Void> response = new ApiResponse<Void>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

