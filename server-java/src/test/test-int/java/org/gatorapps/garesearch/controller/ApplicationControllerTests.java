package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.config.RestDocsConfig;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;


import java.nio.file.Files;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : submitApplication

// TODO : check the database for get, create, and update to ensure actually gets the correct get / updated / created

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationControllerTests  extends BaseTest {
    @Autowired
    private MockMvc mockMvc;

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

    // TODO : write tests for each case of submit . (and label what case is what)
//    @Test // @PostMapping("/application")
    //  @Order(7)
//    public ResponseEntity<ApiResponse<Void>> submitApplication(
//            @RequestParam(value = "positionId", required = true) String positionId,
//            @RequestParam(value = "saveApp", required = false) String saveApp)

//    public void testSubmitApplication() throws Exception {
//        mockMvc.perform(post(applicationControllerRoute + "/submitApplication")
//                        .param("positionId", "6797d2ff9ecab28bd5548672")
//                        .param("saveApp", "false")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
//                .andDo(print())
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.errCode").value("-"))
//
//                .andDo(RestDocsConfig.getDefaultDocHandler("application-submit"));
//    }


    /*------------------------- alreadyApplied -------------------------*/
    // @GetMapping("/alreadyApplied")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> alreadyApplied(HttpServletRequest request,
    //          @RequestParam(value = "positionId", required = true) String positionId) throws Exception {

    @Test
    @Order(3)
    public void getAlreadyApplied_Valid() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/alreadyApplied")
                        .param("positionId", "67dcf54ab42f269d2da84622")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.alreadyApplied").value(true))
                .andDo(RestDocsConfig.getDefaultDocHandler("application-alreadyApplied"));
    }


    /*------------------------- getApplication -------------------------*/
    // @GetMapping("/application")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplication(@Valid HttpServletRequest request,
    //          @RequestParam(value = "labId") String labId,
    //          @RequestParam(value = "applicationId") String applicationId) throws Exception {


    @Test // @GetMapping("/application")
    @Order(4)
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
    @Order(4)
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
    @Order(5)
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
    @Order(6)
    public void updateApplicationStatus_Valid() throws Exception {
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", "abc0c01ab87e195493ae9c10")
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("status", "moving forward")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-update-status"));
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(6)
    public void updateApplicationStatus_InvalidParam() throws Exception {
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", "abc0c01ab87e195493ae9c10")
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .param("status", "delete")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.errMsg").value("Application status must be one of 'submitted', 'archived', or 'moving forward'"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @PutMapping("/applicationStatus")
    @Order(6)
    public void updateApplicationStatus_InvalidLabAccess() throws Exception {
        String response = mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .param("applicationId", "b6c9c01ab87e195493ae9c10")
                        .param("status", "submitted")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access_application.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }
}
