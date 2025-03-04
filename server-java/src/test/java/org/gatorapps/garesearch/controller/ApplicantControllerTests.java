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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ApplicantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String applicantControllerRoute = "/appApi/garesearch/applicant";

    // TODO : write tests expecting exceptions and stuff

    /*------------------------- getApplicantProfile -------------------------*/

    // @GetMapping("/profile") getApplicantProfile()

    @Test // @GetMapping("/profile")
    public void testGetApplicantProfile() throws Exception {
        mockMvc.perform(get(applicantControllerRoute+"/profile"))
                .andDo(print())
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


    /*------------------------- updateApplicantProfile -------------------------*/

    // @PutMapping("/profile")
    //      public ResponseEntity<ApiResponse<Void>> updateApplicantProfile(
    //          @Valid @RequestBody ApplicantProfile applicantProfile)

    @Test // @PutMapping("/profile")
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload").doesNotExist());

    }
}
