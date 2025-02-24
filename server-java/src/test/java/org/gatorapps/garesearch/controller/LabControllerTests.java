package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class LabControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String labControllerRoute = "/appApi/garesearch/lab";

    /*------------------------- getLabPublicProfile -------------------------*/

    // @GetMapping("/single")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabPublicProfile(
    //          @RequestParam(value = "labId", required = true) String labId)

    @Test // @GetMapping("/single")
    public void getPublicProfile_Valid() throws Exception {
        mockMvc.perform(get(labControllerRoute + "/single")
                        .param("labId", "6616be49511f9c6619446abb"))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.labPublicProfile").isNotEmpty())
                .andExpect(jsonPath("$.payload.labPublicProfile.id").value("6616be49511f9c6619446abb"));
    }

    @Test // @GetMapping("/single")
    public void getPublicProfile_ResourceNotFound() throws Exception {
        mockMvc.perform(get(labControllerRoute + "/single")
                        .param("labId", "111111111111111111111111"))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping("/single")
    public void getPublicProfile_MissingParam() throws Exception {
        mockMvc.perform(get(labControllerRoute + "/single"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: labId"));
    }


    /*------------------------- controller function -------------------------*/


}
