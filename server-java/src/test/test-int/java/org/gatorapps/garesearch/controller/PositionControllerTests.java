package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.model.garesearch.Position;
import org.gatorapps.garesearch.repository.garesearch.PositionRepository;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PositionControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PositionRepository positionRepository;

    private final String positionControllerRoute = "/appApi/garesearch/posting";


    /*------------------------- getPositionPublicPosting -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(
    //          @RequestParam(value = "positionId", required = true) String positionId)

    @Test // @GetMapping
    @Order(1)
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

        response = response.replaceAll("\"lastUpdatedTimeStamp\":\".*?\"", "\"lastUpdatedTimeStamp\": \"<timestamp>\"");


        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping
    @Order(1)
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
    @Order(1)
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
    @Order(2)
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
    @Order(2)
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
    @Order(2)
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
    @Order(3)
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
    @Order(4)
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

        response = response.replaceAll("\"lastUpdatedTimeStamp\":\".*?\"", "\"lastUpdatedTimeStamp\": \"<timestamp>\"");

        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    /*------------------------- getPositionPosting -------------------------*/
    // @GetMapping("/postingEditor")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPosting(@Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId")String positionId)

    @Test // @GetMapping("/postingEditor")
    @Order(5)
    public void getProfile_Valid() throws Exception {
        String response = mockMvc.perform(get(positionControllerRoute + "/postingEditor")
                        .param("positionId", "67dcf586c42dde901cd44ef2")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-posting-editor"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/position/get_posting_editor.json").toPath());

        response = response.replaceAll("\"lastUpdatedTimeStamp\":\".*?\"", "\"lastUpdatedTimeStamp\": \"<timestamp>\"");

        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/postingEditor")
    @Order(5)
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
    @Order(5)
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
    @Order(5)
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
    @Order(6)
    public void createNewPosition_Valid() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "88dcf5a77621f49532e47b52",
                    "name": "Literature Review: Mechanical",
                    "description": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
                    "supplementalQuestions": "<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00",
                    "status": "open"
                }
                """);
        mockMvc.perform(post(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-create-new"));


        // uploaded to database correctly
        Position pos = positionRepository.findByName("Literature Review: Mechanical").get();
        assertNotNull(pos, "Position not created to database successfully");

        assertEquals("88dcf5a77621f49532e47b52", pos.getLabId(), "Position labId not set to database successfully");

        assertEquals("<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>", pos.getDescription(), "Position description not set to database successfully");
        assertEquals(Jsoup.parse(pos.getDescription()).text(), pos.getRawDescription(), "Position rawDescription not set to database successfully");
        assertEquals("open", pos.getStatus(), "Position status not set to database successfully");
        assertEquals("<ol><li><strong>Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>", pos.getSupplementalQuestions(), "Position supplementalQuestions not set to database successfully");
    }

    @Test // @PostMapping("/postingEditor")
    @Order(6)
    public void createNewPosition_InvalidLabNotProvided() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "",
                    "name": "new position",
                    "status": "open",
                    "postedTimeStamp":"2025-03-15T21:29:59.062+00:00"
                }
                """);
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
    @Order(6)
    public void createNewPosition_InvalidLabAccess() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "99dcf5a77621f49532e47b52",
                    "name": "createNewPosition_InvalidLabAccess",
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

        // ensure not uploaded to database
        assertFalse(positionRepository.existsByName("createNewPosition_InvalidLabAccess"), "Position uploaded to database when it shouldnt");

    }


    /*------------------------- updatePositionPosting -------------------------*/
    // @PutMapping("/postingEditor")
    //    public ResponseEntity<ApiResponse<Void>> updatePositionPosting(@Valid HttpServletRequest request,
    //          @RequestBody Position position) throws Exception {

    @Test // @PostMapping("/postingEditor")
    @Order(7)
    public void updatePosition_Valid() throws Exception {
        String requestBody = String.format("""
                {
                    "id": "67dcf57f1d3d5f5c7fcc5645",
                    "labId": "88dcf5a77621f49532e47b52",
                    "name": "Ava's Updated Test Position: 55",
                    "description": "<p><strong>Updated Description</strong><br></p>",
                    "supplementalQuestions": "<ol><li><strong>NEW Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>",
                    "status": "open"
                }
                """);
        mockMvc.perform(put(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-update-existing"));


        // updated to database correctly
        Position pos = positionRepository.findById("67dcf57f1d3d5f5c7fcc5645").get();
        assertNotNull(pos, "Position not exists in database");

        assertEquals("88dcf5a77621f49532e47b52", pos.getLabId(), "Position labId not equal");
        assertEquals("Ava's Updated Test Position: 55", pos.getName(), "Position name not updated correctly");

        assertEquals("<p><strong>Updated Description</strong><br></p>", pos.getDescription(), "Position Description not updated correctly");
        assertEquals(Jsoup.parse(pos.getDescription()).text(), pos.getRawDescription(), "Position rawDescription not updated correctly");
        assertEquals("<ol><li><strong>NEW Tell us about your current interests and any related extracurricular ?</strong></li><li><strong>Do you have previous research experience?</strong></li><li><strong>Why are you interested in this position ?</strong></li></ol>", pos.getSupplementalQuestions(), "Position supplementalQuestions not updated correctly");
        assertEquals("open", pos.getStatus(), "Position status not updated correctly");
    }


    @Test // @PostMapping("/postingEditor")
    @Order(7)
    public void updateNewPosition_PositionIdNotProvided() throws Exception {
        String requestBody = String.format("""
                {
                    "labId": "88dcf5a77621f49532e47b52",
                    "name": "updateNewPosition_PositionIdNotProvided"
                }
                """);
        mockMvc.perform(put(positionControllerRoute + "/postingEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }


    @Test // @PostMapping("/postingEditor")
    @Order(7)
    public void updateNewPosition_InvalidLabAccess() throws Exception {
        String requestBody = String.format("""
                {
                    "id": "67dcf54ab42f269d2da84622",
                    "labId": "99dcf5a77621f49532e47b52",
                    "name": "updateNewPosition_invalidLabAccess"
                }
                """);
        String response = mockMvc.perform(put(positionControllerRoute + "/postingEditor")
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

        // ensure did not update to database
        Position pos = positionRepository.findById("67dcf54ab42f269d2da84622").get();
        assertNotNull(pos, "Position not exists in database");

        assertEquals("99dcf5a77621f49532e47b52", pos.getLabId(), "Position labId not equal");
        assertNotEquals("updateNewPosition_invalidLabAccess", pos.getName(), "Position name updated when it shouldnt have");
    }


    /*------------------------- updatePositionStatus -------------------------*/
    // @PutMapping("/postingStatus")
    //    public ResponseEntity<ApiResponse<Void>> updatePositionStatus(
    //          @Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId") String positionId,
    //          @RequestParam(value = "status") @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'") String status)

    @Test // @PutMapping("/postingStatus")
    @Order(8)
    public void updatePostingStatus_Valid() throws Exception {
        String positionId = "c163c180ed0a40bea3393855";
        String status = "archived";

        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", positionId)
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-update-status"));

        // updated to database correctly
        Position pos = positionRepository.findById(positionId).get();
        assertNotNull(pos, "Position not exists in database");

        assertEquals(status, pos.getStatus(), "status not updated correctly to database");
    }

    @Test // @PutMapping("/postingStatus")
    @Order(8)
    public void updatePostingStatus_InvalidParam() throws Exception {
        String positionId = "d162c110ed0a40bea3393855";
        String status = "deleted";
        Position position = positionRepository.findById(positionId).get();
        String oldStatus = position.getStatus();

        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", positionId)
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.errMsg").value("Position status must be one of 'open', 'closed', 'archived'"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure position was not updated
        Position pos = positionRepository.findById(positionId).get();
        assertNotNull(pos, "Position not exists in database");
        assertEquals(oldStatus, pos.getStatus(), "status was changed when it shouldnt have");
        assertNotEquals(status, pos.getStatus(), "status was updated when it shouldnt have");
    }

    @Test // @PutMapping("/postingStatus")
    @Order(8)
    public void updatePostingStatus_InvalidLabAccess() throws Exception {
        String positionId = "67dcf54ab42f269d2da84622";
        String status = "closed";
        Position position = positionRepository.findById(positionId).get();
        String oldStatus = position.getStatus();

        String response = mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .param("status", "closed"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_position.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);

        // ensure position was not updated
        Position pos = positionRepository.findById(positionId).get();
        assertNotNull(pos, "Position not exists in database");
        assertEquals(oldStatus, pos.getStatus(), "status was changed when it shouldnt have");
        assertNotEquals(status, pos.getStatus(), "status was updated when it shouldnt have");
    }


    @Test // @PutMapping("/postingStatus")
    @Order(8)
    public void updatePostingStatus_MissingParamStatus() throws Exception {
        String positionId = "d162c110ed0a40bea3393855";
        Position position = positionRepository.findById(positionId).get();
        String oldStatus = position.getStatus();

        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("positionId", positionId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: status"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure position was not updated
        Position pos = positionRepository.findById(positionId).get();
        assertNotNull(pos, "Position not exists in database");
        assertEquals(oldStatus, pos.getStatus(), "status was changed when it shouldnt have");
    }

    @Test // @PutMapping("/postingStatus")
    @Order(8)
    public void updatePostingStatus_MissingParamPositionId() throws Exception {
        mockMvc.perform(put(positionControllerRoute + "/postingStatus")
                        .param("status", "open")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }



    // searching functionality uses mongo atlas search indices which are not available through docker test db. Was tested on dev db and can be manually tested through postman

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



    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)


}
