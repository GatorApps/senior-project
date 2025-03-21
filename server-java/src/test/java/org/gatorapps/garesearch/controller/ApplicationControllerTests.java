package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : redo tests to accomodate testcontainer (change out ids)

// TODO : applicationManagement, single application (faculty)

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class ApplicationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    private final String applicationControllerRoute = "/appApi/garesearch/application";


    /*------------------------- getStudentApplication -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(
    //          @RequestParam(value = "applicationId", required = true) String applicationId)
/*
    @Test // @GetMapping
    public void getStuApplication_Valid() throws Exception {
        mockMvc.perform(get(applicationControllerRoute)
                        .param("applicationId", "67be553bd7565c4e30236224")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.application").isNotEmpty())
                .andExpect(jsonPath("$.payload.application.applicationId").value("67be553bd7565c4e30236224"))
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-by-id"));
    }

    @Test // @GetMapping
    public void getStuApplication_ResourceNotFound() throws Exception {
        mockMvc.perform(get(applicationControllerRoute)
                        .param("applicationId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping
    public void getStuApplication_MissingParam() throws Exception {
        mockMvc.perform(get(applicationControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: applicationId"));
    }

    */

    /*------------------------- getStudentApplications -------------------------*/

    // @GetMapping("/studentList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications()

    @Test // @GetMapping("/studentList")
    public void getStuApplications_Valid() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/studentList")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.applications").isMap())
                .andExpect(jsonPath("$.payload.applications.activeApplications").isArray())
                .andExpect(jsonPath("$.payload.applications.archivedApplications").isArray())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-get-studentList"));
    }


    /*------------------------- submitApplication -------------------------*/

    // TODO : write tests for each case of submit . (and label what case is what)
//    @Test // @PostMapping("/application")
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



    /*------------------------- updatePositionStatus -------------------------*/
    // @PutMapping("/applicationStatus")
    //    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
    //          @Valid HttpServletRequest request,
    //          @RequestParam(value = "positionId") String positionId,
    //          @RequestParam(value = "applicationId") String applicationId,
    //          @RequestParam(value = "status") @Pattern(regexp = "submitted|archived", message = "Application status must be one of 'submitted', 'archived'") String status)

    @Test // @PutMapping("/applicationStatus")
    public void updatePostingStatus_Valid() throws Exception {
        mockMvc.perform(put(applicationControllerRoute + "/applicationStatus")
                        .param("applicationId", "67d5e22d11bf542d9f56f66b")
                        .param("positionId", "67c3c01ab87e185493ae9c10")
                        .param("status", "submitted")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("application-update-status"));
    }

}
