package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.repository.garesearch.ApplicationRepository;
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
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationControllerTests  extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApplicationRepository applicationRepository;

    private final String applicationControllerRoute = "/appApi/garesearch/application";


    /*------------------------- getStudentApplication -------------------------*/
    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(HttpServletRequest request,
    //          @RequestParam(value = "applicationId", required = true) String applicationId) throws Exception

    @Test // @GetMapping
    @Order(1)
    public void getStuApplication_Valid() throws Exception {
        String response = mockMvc.perform(get(applicationControllerRoute)
                        .param("applicationId", "b1c9c01ab87e195493ae9b56")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-by-id"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/application/get_application_by_id.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping
    @Order(1)
    public void getStuApplication_ResourceNotFound() throws Exception {
        mockMvc.perform(get(applicationControllerRoute)
                        .param("applicationId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Application Not Found"));
    }

    @Test // @GetMapping
    @Order(1)
    public void getStuApplication_MissingParam() throws Exception {
        mockMvc.perform(get(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: applicationId"));
    }



    /*------------------------- getStudentApplications -------------------------*/

    // @GetMapping("/studentList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications()

    @Test // @GetMapping("/studentList")
    @Order(2)
    public void getStuApplications_Valid() throws Exception {
        String response = mockMvc.perform(get(applicationControllerRoute + "/studentList")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-studentList"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/application/get_student_list.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    /*------------------------- submitApplication -------------------------*/
    // @PostMapping
    //    public ResponseEntity<ApiResponse<Void>> submitApplication(
    //            @Valid HttpServletRequest request,
    //            @RequestParam(value = "positionId", required = true) String positionId,
    //            @RequestBody(required = true) Map<String, Object> requestBody)

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_Valid() throws Exception {
        String requestBody = String.format("""
                    { 
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);
        String positionId = "67dcf572d600cb3d03968c0f";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .param("positionId", positionId))
                .andDo(print())

                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-post-submit"));

        // uploaded to database correctly
        Application app = applicationRepository.findByOpidAndPositionId(TEST_USER_OPID, positionId).get();
        assertNotNull(app, "not uploaded to database correctly");
        assertEquals("1a1cf5a77621f49532e47b52", app.getResumeId(), "not uploaded to database correctly");
        assertEquals("1a2cf5a77621f49532e47b52", app.getTranscriptId(), "not uploaded to database correctly");
    }



    /*  EXCEPTIONS TO CHECK
            - position id does not exst
            - position status is NOT open -- 67dcf586c42dde901cd44ef2
            - user already applied to the position -- 67dcf54ab42f269d2da84622
            - resume id not found
            - resume's opid is not linked to same applicant
            - transcript id not found
            - transcript's opid is not linked to same applicant
    */

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_MissingParamPositionId() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_InvalidPositionId() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);
        String positionId = "111111111111111111111111";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .param("positionId", positionId))
                .andDo(print())

                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("PositionId " + positionId + " does not exist"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_PositionNotOpen() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);

        String positionId = "67dcf578705c5f00403f2e85";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().isBadRequest()) // 500
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Position " + positionId + " is not open"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_alreadyApplied() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);

        String positionId = "67dcf56a264b9db3ef415cb2";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andDo(print())
                .andExpect(status().isBadRequest()) // 500
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("You have already applied to this position"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure we did apply already
        assertTrue(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application for this position does not exist in database, but test ran as if it did");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_resumeIdNotFound() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "111111111111111111111111",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);

        String positionId = "67dcf55ce8e62d8cafd302ee";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid resumeId (111111111111111111111111), please try again"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_resumeMismatchedUser() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "aa1cf5a77621f49532e47b52",
                            "transcriptId": "1a2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);

        String positionId = "67dcf55ce8e62d8cafd302ee";
        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().is4xxClientError()) // 400
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid resumeId (aa1cf5a77621f49532e47b52), please try again"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_transcriptIdNotFound() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "111111111111111111111111",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);
        String positionId = "67dcf55ce8e62d8cafd302ee";

        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid transcriptId (111111111111111111111111), please try again"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }

    @Test // @PostMapping
    @Order(3)
    public void submitApplication_transcriptMismatchedUser() throws Exception {
        String requestBody = String.format("""
                    {
                        "application": {
                            "resumeId": "1a1cf5a77621f49532e47b52",
                            "transcriptId": "aa2cf5a77621f49532e47b52",
                            "supplementalResponses": "<p><strong>Responses to Supplemental Questions !</strong></p"
                        }
                    }
                    """);
        String positionId = "67dcf55ce8e62d8cafd302ee";

        mockMvc.perform(post(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .param("positionId", positionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())

                .andExpect(status().is4xxClientError()) // 400
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid transcriptId (aa2cf5a77621f49532e47b52), please try again"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure not uploaded to database
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application uploaded to database when it shouldnt");
    }



    /*------------------------- alreadyApplied -------------------------*/
    // @GetMapping("/alreadyApplied")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> alreadyApplied(HttpServletRequest request,
    //          @RequestParam(value = "positionId", required = true) String positionId) throws Exception {

    @Test
    @Order(4)
    public void getAlreadyApplied_Valid() throws Exception {
        String positionId = "67dcf54ab42f269d2da84622";
        mockMvc.perform(get(applicationControllerRoute + "/alreadyApplied")
                        .param("positionId", positionId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.alreadyApplied").value(true))
                .andDo(RestDocsConfig.getDefaultDocHandler("application-alreadyApplied"));

        // ensure we did apply already
        assertTrue(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, positionId), "Application for this position does not exist in database, but test ran as if it did");
    }


    /*------------------------- getApplication -------------------------*/
    // @GetMapping("/application")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplication(@Valid HttpServletRequest request,
    //          @RequestParam(value = "labId") String labId,
    //          @RequestParam(value = "applicationId") String applicationId) throws Exception {


    @Test // @GetMapping("/application")
    @Order(5)
    public void getApplication_Valid() throws Exception {
        String response = mockMvc.perform(get(applicationControllerRoute + "/application")
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("applicationId", "abc0c01ab87e195493ae9c10")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-faculty"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/application/get_application_faculty.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    @Test // @GetMapping("/application")
    @Order(5)
    public void getApplication_InvalidLabAccess() throws Exception {
        String response = mockMvc.perform(get(applicationControllerRoute + "/application")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .param("applicationId", "b6c9c01ab87e195493ae9c10")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    /*------------------------- getApplicationList -------------------------*/
    // @GetMapping("/applicationManagement")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationList(HttpServletRequest request,
    //          @RequestParam (value="positionId") String positionId) throws Exception {

    @Test // @GetMapping("/applicationManagement")
    @Order(6)
    public void getApplicationManagement_Valid() throws Exception {
        String response = mockMvc.perform(get(applicationControllerRoute + "/applicationManagement")
                        .param("positionId", "87d0c01ab87e195493ae9c10")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-mgmt-list"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();



        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/application/get_application_mgmt_list.json").toPath());
        response = response.replaceAll("\"submissionTimeStamp\":\".*?\"", "\"submissionTimeStamp\": \"<timestamp>\"");
        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    /*------------------------- updateApplicationStatus -------------------------*/
    // @PutMapping("/applicationStatus")
    //    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(@Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId") String positionId,
    //          @RequestParam(value = "applicationId") String applicationId,
    //          @RequestParam(value = "status") @Pattern(regexp = "submitted|archived|moving forward", message = "Application status must be one of 'submitted', 'archived', or 'moving forward'") String status) throws Exception {

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_Valid() throws Exception {
        String applicationId = "abc0c01ab87e195493ae9c10";
        String status = "moving forward";
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", applicationId)
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-update-status"));

        // updated to database correctly
        Application app = applicationRepository.findById(applicationId).get();
        assertNotNull(app, "Application not exists in database");
        assertEquals(status, app.getStatus(), "status not updated correctly to database");
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_appNotFound() throws Exception {
        String applicationId = "111111111111111111111111";
        String status = "moving forward";
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", applicationId)
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isNotFound())  // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Application " + applicationId + " not found"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure application does not exist
        assertFalse(applicationRepository.existsByOpidAndPositionId(TEST_USER_OPID, applicationId), "Application exists in database, but test ran as if it didnt");
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_InvalidParam() throws Exception {
        String applicationId = "abc0c01ab87e195493ae9c10";
        String status = "delete";

        Application application = applicationRepository.findById(applicationId).get();
        String oldStatus = application.getStatus();

        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", applicationId)
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.errMsg").value("Application status must be one of 'submitted', 'archived', or 'moving forward'"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure application was not updated
        Application application2 = applicationRepository.findById(applicationId).get();
        assertEquals(oldStatus, application2.getStatus(), "status was changed when it shouldnt have");
        assertNotEquals(status, application2.getStatus(), "status was updated when it shouldnt have");
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_InvalidLabAccess() throws Exception {
        String applicationId = "b6c9c01ab87e195493ae9c10";
        String status = "archived";

        Application application = applicationRepository.findById(applicationId).get();
        String oldStatus = application.getStatus();

        String response = mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .param("applicationId", applicationId)
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_application.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);

        // ensure application was not updated
        Application application2 = applicationRepository.findById(applicationId).get();
        assertEquals(oldStatus, application2.getStatus(), "status was changed when it shouldnt have");
        assertNotEquals(status, application2.getStatus(), "status was updated when it shouldnt have");
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_MissingParamLabId() throws Exception {
        String applicationId = "abc0c01ab87e195493ae9c10";
        String status = "archived";

        Application application = applicationRepository.findById(applicationId).get();
        String oldStatus = application.getStatus();

        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", applicationId)
                        .param("status", status)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: labId"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure application was not updated
        Application application2 = applicationRepository.findById(applicationId).get();
        assertEquals(oldStatus, application2.getStatus(), "status was changed when it shouldnt have");
        assertNotEquals(status, application2.getStatus(), "status was updated when it shouldnt have");
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_MissingParamAppId() throws Exception {
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .param("status", "delete")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: applicationId"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }


    @Test // @PutMapping("/applicationStatus")
    @Order(7)
    public void updateApplicationStatus_MissingParamStatus() throws Exception {
        String applicationId = "abc0c01ab87e195493ae9c10";
        Application application = applicationRepository.findById(applicationId).get();
        String oldStatus = application.getStatus();

        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .param("applicationId", applicationId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: status"))
                .andExpect(jsonPath("$.payload").isEmpty());

        // ensure application was not updated
        Application application2 = applicationRepository.findById(applicationId).get();
        assertEquals(oldStatus, application2.getStatus(), "status was changed when it shouldnt have");
    }



}
