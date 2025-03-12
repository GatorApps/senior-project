package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Education;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class ApplicantControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String applicantControllerRoute = "/appApi/garesearch/applicant";

    /*------------------------- getApplicantProfile -------------------------*/

    // @GetMapping("/profile") getApplicantProfile()

    @Test // @GetMapping("/profile")
    public void testGetApplicantProfile() throws Exception {
        mockMvc.perform(get(applicantControllerRoute+"/profile")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload.applicantProfile.data").exists())
                .andExpect(jsonPath("$.payload.applicantProfile.update.endpoint.method").value("put"))
                .andExpect(jsonPath("$.payload.applicantProfile.update.endpoint.route").value("/applicant/profile"))
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-profile-get"));
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
//        applicantProfile.setEducation(educations);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(applicantProfile);

        mockMvc.perform(put(applicantControllerRoute + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest) // Mock the request body
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload").doesNotExist())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-profile-put"));
    }
}
