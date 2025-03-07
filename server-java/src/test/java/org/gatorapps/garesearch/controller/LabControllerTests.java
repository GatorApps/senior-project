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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class LabControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String labControllerRoute = "/appApi/garesearch/lab";

    /*------------------------- getLabPublicProfile -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabPublicProfile(
    //          @RequestParam(value = "labId", required = true) String labId)

    @Test // @GetMapping
    public void getPublicProfile_Valid() throws Exception {
        mockMvc.perform(get(labControllerRoute)
                        .param("labId", "6797d2ff9ecab28bd5548672")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.lab").isNotEmpty())
                .andExpect(jsonPath("$.payload.lab.labId").value("6797d2ff9ecab28bd5548672"))
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-get-by-id"));
    }



    @Test // @GetMapping
    public void getPublicProfile_ResourceNotFound() throws Exception {
        mockMvc.perform(get(labControllerRoute)
                        .param("labId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping
    public void getPublicProfile_MissingParam() throws Exception {
        mockMvc.perform(get(labControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: labId"));
    }


    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)





}
