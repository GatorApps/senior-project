package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Education;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String applicantControllerRoute = "/appApi/garesearch/applicant";

    // TODO : write tests expecting exceptions and stuff

    @Test // @GetMapping("/profile") getApplicantProfile()
    public void testGetApplicantProfile() throws Exception {
        mockMvc.perform(get(applicantControllerRoute+"/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload.applicantProfile.data").exists()) // can make more specific like opid once that is figured out
                .andExpect(jsonPath("$.payload.applicantProfile.update.endpoint.method").value("put"))
                .andExpect(jsonPath("$.payload.applicantProfile.update.endpoint.route").value("/applicant/profile"));
    }
    /* Response is a lot of nested jsons
            {
                errCode: '0',
                payload: {
                    applicantProfile: {
                        data: foundProfile,
                        update: {
                            endpoint: {
                                method: "put",
                                route: "/applicant/profile"
                            }
                        }
                    }
                }
            }
     */


    @Test // @PutMapping("/profile")
    //public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(@Valid @RequestBody ApplicantProfile applicantProfile)
    public void testPutApplicantProfile() throws Exception {
        ApplicantProfile applicantProfile = new ApplicantProfile();
        applicantProfile.setOpid("127ad6f9-a0ff-4e3f-927f-a70b64c542e4");
        List<Education> educations = new ArrayList<>();
        educations.add(new Education("UF", new Date(), new Date(new Date().getTime() + 600000), "BS", "CS", "TESTING"));
        educations.add(new Education("NYU", new Date(), new Date(new Date().getTime() + 600000), "Masters of Science", "Cyber", "TESTING"));
        applicantProfile.setEducation(educations);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(applicantProfile);

        mockMvc.perform(put(applicantControllerRoute + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))  // Mock the request body
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload").doesNotExist());

    }

    @Test // @GetMapping("/applications")
    // public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentApplications(
    //          @RequestParam(required=true)
    //          @Pattern(regexp = "saved|active|inactive", message = "Status must be one of 'saved', 'active', 'inactive'")
    //          String status )
    public void testGetStudentApplications() throws Exception {
        mockMvc.perform(get(applicantControllerRoute + "/applications")
                        .param("status", "active"))
                .andExpect(status().isOk())  // Check for HTTP 200 OK
                .andExpect(jsonPath("$.payload.applications").isArray());
        // TODO : looks correct . but write actual check for correct structure.
    }


    // TODO : write tests for each case of submit . (and label what case is what)
    @Test // @PostMapping("/application")
//    public ResponseEntity<ApiResponse<Void>> submitApplication(
//            @RequestParam(value = "positionId", required = true) String positionId,
//            @RequestParam(value = "saveApp", required = false) String saveApp)
    public void testSubmitApplication() throws Exception {
        mockMvc.perform(post(applicantControllerRoute + "/application")
                        .param("positionId", "6797d2a79ecab28bd554866b")
                        .param("saveApp", "false"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errCode").value("-"));
    }


}
