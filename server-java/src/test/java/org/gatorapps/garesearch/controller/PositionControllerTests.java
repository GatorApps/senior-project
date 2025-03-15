package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class PositionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String positionControllerRoute = "/appApi/garesearch/posting";



    /*------------------------- getSearchResults -------------------------*/

    // @GetMapping("/searchList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
    //          @RequestParam(value = "searchParams") String searchParams)

    @Test // @GetMapping("/searchList")
    public void getSearchResults_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/searchList")
                        .param("q", "lu")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.positions").isArray())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchresults"));
    }

    /*------------------------- getSearchIndexerResults -------------------------*/

    // @GetMapping("/searchIndexer")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
    //          @RequestParam(value = "searchParams") String searchParams)
    @Test // @GetMapping("/searchList")
    public void getSearchIndexerResults_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/searchIndexer")
                        .param("q", "lu 7")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.positions").isArray())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchresults_indexer"));
    }


    /*------------------------- getPositionPublicPosting -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(
    //          @RequestParam(value = "positionId", required = true) String positionId)

    @Test // @GetMapping
    public void getPublicPosting_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .param("positionId", "67c3c01ab87e185493ae9c10")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.position").isNotEmpty())
                .andExpect(jsonPath("$.payload.position.positionId").value("67c3c01ab87e185493ae9c10"))
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-posting-by-id"));
    }

    @Test // @GetMapping
    public void getPublicPosting_ResourceNotFound() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .param("positionId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping("/single")
    public void getPublicPosting_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
    }


    /*------------------------- getFacultyPostingsList -------------------------*/
    // @GetMapping("/postingManagement")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getFacultyPostingsList(
    //          @Valid HttpServletRequest request)

    @Test // @GetMapping("/postingManagement")
    public void getPostingsList_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/postingManagement")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.postingsList").exists())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-management-list"));
    }

    /*------------------------- updatePositionStatus -------------------------*/
    // @PutMapping("/postingStatus")
    //    public ResponseEntity<ApiResponse<Void>> updatePositionStatus(
    //          @Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId") String positionId,
    //           @RequestParam(value = "status") @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'") String status)

    @Test // @PutMapping("/postingStatus")
    public void updatePostingStatus_Valid() throws Exception {
        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", "67d5d23345ad7501f03159cf")
                        .param("status", "open")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-update-status"));
    }

    /*------------------------- savePositionPosting -------------------------*/
    // @PostMapping("/postingEditor")
    //    public ResponseEntity<ApiResponse<Void>> savePositionPosting(
    //          @Valid HttpServletRequest request,
    //          @Valid @RequestBody Position position)


    @Test // @PostMapping("/postingEditor")
    public void createNewPosition_Valid() throws Exception {
        String requestBody = String.format("""
                    {
                        "labId": "67d27e5be0587166932d7984",
                        "name": "Ava's Test Position %d",
                        "description": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
                        "supplementalQuestions": "<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                        "status": "open"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-create-new"));
    }

    @Test // @PostMapping("/postingEditor")
    public void updatePosition_Valid() throws Exception {
        String requestBody = String.format("""
                    {
                        "id": "67d5d23345ad7501f03159cf",
                        "labId": "67d27e5be0587166932d7984",
                        "name": "Ava's Test Position %d",
                        "description": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
                        "supplementalQuestions": "<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                        "status": "open"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-update-existing"));
    }

    @Test // @PostMapping("/postingEditor")
    public void createNewPosition_invalid() throws Exception {
        String requestBody = String.format("""
                    {
                        "labId": "",
                        "name": "new position %d",
                        "status": "open"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.payload.labId").isNotEmpty());
    }

    @Test // @PostMapping("/postingEditor")
    public void updateNewPosition_invalidLabAccess() throws Exception {
        String requestBody = String.format("""
                    {
                        "id": "67d5bcd9f7ef8c4aece255ae",
                        "labId": "67d5c0f411bf542d9f56f648",
                        "name": "new position %d",
                        "status": "open"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_INVALID_ACCESS"))
                .andExpect(jsonPath("$.errMsg").isNotEmpty());
    }




    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)



}