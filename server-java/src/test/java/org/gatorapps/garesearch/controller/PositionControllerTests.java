package org.gatorapps.garesearch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PositionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String positionControllerRoute = "/appApi/garesearch/position";

    /*------------------------- getPositionPublicPosting -------------------------*/

    // @GetMapping("/single")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(
    //          @RequestParam(value = "positionId", required = true) String positionId)

    @Test // @GetMapping("/single")
    public void getPublicPosting_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/single")
                        .param("positionId", "6622b8fcceb54473205d08bf"))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.positionPublicPosting").isNotEmpty())
                .andExpect(jsonPath("$.payload.positionPublicPosting.id").value("6622b8fcceb54473205d08bf"));
    }


    @Test // @GetMapping("/single")
    public void getPublicPosting_ResourceNotFound() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/single")
                        .param("positionId", "111111111111111111111111"))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping("/single")
    public void getPublicPosting_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/single"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
    }

    /*------------------------- controller function -------------------------*/


}