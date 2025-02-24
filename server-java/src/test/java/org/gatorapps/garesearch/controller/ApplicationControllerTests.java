package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Application;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Education;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String applicationControllerRoute = "/appApi/garesearch/application";


    /*------------------------- getStudentApplication -------------------------*/

    // @GetMapping("/single")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplication(
    //          @RequestParam(value = "applicationId", required = true) String applicationId)

    @Test // @GetMapping("/single")
    public void getStuApplication_Valid() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/single")
                        .param("applicationId", "6797d2359ecab28bd5548664"))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.application").isNotEmpty())
                .andExpect(jsonPath("$.payload.application.id").value("6797d2359ecab28bd5548664"));
    }

    @Test // @GetMapping("/single")
    public void getStuApplication_ResourceNotFound() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/single")
                        .param("applicationId", "111111111111111111111111"))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping("/single")
    public void getStuApplication_MissingParam() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/single"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: applicationId"));
    }

    /*------------------------- getStudentApplications -------------------------*/

    // @GetMapping("/applications")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications(
    //          @RequestParam(required=true)
    //          @Pattern(regexp = "saved|active|inactive", message = "Status must be one of 'saved', 'active', 'inactive'")
    //          String status )

    @Test // @GetMapping("/applications")
    public void getStuApplications_Valid() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/stuList")
                        .param("status", "active"))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.applications").isArray());
    }

    @Test // @GetMapping("/applications")
    public void getStuApplications_MissingParam() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/stuList"))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: status"));
    }


    @Test // @GetMapping("/applications")
    public void getStuApplications_InvalidParam() throws Exception {
        mockMvc.perform(get(applicationControllerRoute + "/stuList")
                        .param("status", "kdlfajkd"))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.errMsg").value("Status must be one of 'saved', 'active', 'inactive'"));
    }








    /*------------------------- submitApplication -------------------------*/

    // TODO : write tests for each case of submit . (and label what case is what)
    @Test // @PostMapping("/application")
//    public ResponseEntity<ApiResponse<Void>> submitApplication(
//            @RequestParam(value = "positionId", required = true) String positionId,
//            @RequestParam(value = "saveApp", required = false) String saveApp)
    public void testSubmitApplication() throws Exception {
        mockMvc.perform(post(applicationControllerRoute + "/stuList")
                        .param("positionId", "6797d2a79ecab28bd554866b")
                        .param("saveApp", "false"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errCode").value("-"));
    }


}
