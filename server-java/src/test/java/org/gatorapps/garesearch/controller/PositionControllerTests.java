package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.service.PositionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.util.Random;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

// TODO : fix searching functionality. will have to mock service likely. since no atlas search index can be created for testing


// TODO : check the database for get, create, and update to ensure actually gets the correct get / updated / created

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class PositionControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PositionService positionService;

    private final String positionControllerRoute = "/appApi/garesearch/posting";



    /*------------------------- getSearchResults -------------------------*/

    // @GetMapping("/searchList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
    //          @RequestParam(value = "searchParams") String searchParams)

//    @Test // @GetMapping("/searchList")
//    public void getSearchResults_Valid() throws Exception {
//        //when(positionService.getSearchResults(anyString())).thenReturn();
//        mockMvc.perform(get(positionControllerRoute + "/searchList")
//                        .param("q", "lu 2")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk())  // 200
//                .andExpect(jsonPath("$.payload.positions").isArray());
//                //.andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchresults"));
//    }
//
//    /*------------------------- getSearchIndexerResults -------------------------*/
//
//    // @GetMapping("/searchIndexer")
//    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
//    //          @RequestParam(value = "searchParams") String searchParams)
//    @Test // @GetMapping("/searchList")
//    public void getSearchIndexerResults_Valid() throws Exception {
//        mockMvc.perform(get(positionControllerRoute + "/searchIndexer")
//                        .param("q", "lu 7")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk())  // 200
//                .andExpect(jsonPath("$.payload.positions").isArray())
//                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchresults_indexer"));
//    }


    /*------------------------- getPositionPublicPosting -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(
    //          @RequestParam(value = "positionId", required = true) String positionId)

    @Test // @GetMapping
    public void getPublicPosting_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute)
                        .param("positionId", "d162c110ed0a40bea3393855")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-posting-by-id"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_public_posting.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);

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
                .andExpect(jsonPath("$.errMsg").value("Position Not Found"));
    }

    @Test // @GetMapping
    public void getPublicPosting_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
    }

    /*------------------------- getSupplementalQuestions -------------------------*/
    // @GetMapping("/supplementalQuestions")
    //    public ResponseEntity<?> getSupplementalQuestions(
    //          @RequestParam(value = "positionId") String positionId) throws Exception {

    @Test // @GetMapping("/supplementalQuestions")
    public void getSupplementalQuestions_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/supplementalQuestions")
                        .param("positionId", "d162c110ed0a40bea3393855")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-supplemental-questions"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_supplemental_questions.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    @Test // @GetMapping("/supplementalQuestions")
    public void getSupplementalQuestions_ResourceNotFound() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/supplementalQuestions")
                        .param("positionId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())  // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Position Not Found"));
    }

    @Test // @GetMapping("/supplementalQuestions")
    public void getSupplementalQuestions_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
    }


    /*------------------------- getPositionsNamesList -------------------------*/
    // @GetMapping("/postingsList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionsNamesList(HttpServletRequest request)

    @Test // @GetMapping("/postingsList")
    public void getPostingsNamesList_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/postingsList")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-names-list"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_postings_names_list.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    /*------------------------- getFacultyPostingsList -------------------------*/
    // @GetMapping("/postingManagement")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getFacultyPostingsList(
    //          @Valid HttpServletRequest request)

    @Test // @GetMapping("/postingManagement")
    public void getPostingManagement_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/postingManagement")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-management-list"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_posting_mgmt_list.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    /*------------------------- getPositionPosting -------------------------*/
    // @GetMapping("/postingEditor")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPosting(@Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId")String positionId)

    @Test // @GetMapping("/postingEditor")
    public void getProfile_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/postingEditor")
                        .param("positionId", "d162c110ed0a40bea3393855")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-posting-editor"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_posting_editor.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/postingEditor")
    public void getProfile_InvalidLabAccess() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/postingEditor")
                        .param("positionId", "67dcf54ab42f269d2da84622")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_position.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    @Test // @GetMapping("/postingEditor")
    public void getProfile_ResourceNotFound() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/postingEditor")
                        .param("positionId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Position Not Found"));
    }

    @Test // @GetMapping("/postingEditor")
    public void getProfile_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
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
                    "labId": "88dcf5a77621f49532e47b52",
                    "name": "Ava's Test Position %d",
                    "description": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
                    "supplementalQuestions": "<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00",
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
    public void createNewPosition_Invalid() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "",
                    "name": "new position %d",
                    "status": "open",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00"
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
    public void createNewPosition_InvalidLabAccess() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "99dcf5a77621f49532e47b52",
                    "name": "New position %d",
                    "status": "open",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00"
                }
                """, new Random().nextInt(1000));
        String response = mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_position.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    /*------------------------- updatePositionPosting -------------------------*/
    // @PutMapping("/postingEditor")
    //    public ResponseEntity<ApiResponse<Void>> updatePositionPosting(@Valid HttpServletRequest request,
    //          @RequestBody Position position) throws Exception {

    @Test // @PostMapping("/postingEditor")
    public void updatePosition_Valid() throws Exception {
        String requestBody = String.format("""
                {
                    "id": "67dcf57f1d3d5f5c7fcc5645",
                    "labId": "88dcf5a77621f49532e47b52",
                    "name": "Ava's Updated Test Position %d",
                    "description": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
                    "supplementalQuestions": "<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00",
                    "status": "open"
                }
                """, new Random().nextInt(1000));
        mockMvc.perform(put(positionControllerRoute + "/postingEditor")
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
    public void updateNewPosition_invalidLabAccess() throws Exception {
        String requestBody = String.format("""
                {
                    "id": "67dcf54ab42f269d2da84622",
                    "labId": "99dcf5a77621f49532e47b52",
                    "name": "new position",
                    "status": "open",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00"
                }
                """);
        String response = mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_position.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    /*------------------------- updatePositionStatus -------------------------*/
    // @PutMapping("/postingStatus")
    //    public ResponseEntity<ApiResponse<Void>> updatePositionStatus(
    //          @Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId") String positionId,
    //          @RequestParam(value = "status") @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'") String status)

    @Test // @PutMapping("/postingStatus")
    public void updatePostingStatus_Valid() throws Exception {
        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", "d162c110ed0a40bea3393855")
                        .param("status", "open")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-update-status"));
    }

    @Test // @PutMapping("/postingStatus")
    public void updatePostingStatus_InvalidParam() throws Exception {
        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", "d162c110ed0a40bea3393855")
                        .param("status", "deleted")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.errMsg").value("Position status must be one of 'open', 'closed', 'archived'"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @PutMapping("/postingStatus")
    public void updatePostingStatus_InvalidLabAccess() throws Exception {
        String response = mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", "67dcf54ab42f269d2da84622")
                        .param("status", "closed"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_position.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }





    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)


}
