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
public class PositionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final String positionControllerRoute = "/appApi/garesearch/posting";



    /*------------------------- getSearchResults -------------------------*/

    // @GetMapping("/searchList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
    //          @RequestParam(value = "searchParams") String searchParams)

    @Test // @GetMapping("/searchList")
    public void getSearchResults_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/searchList")
                        .param("q", "lu")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.positions").isArray())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchResults"));
    }

    /*------------------------- getSearchIndexerResults -------------------------*/

    // @GetMapping("/searchIndexer")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchResults(
    //          @RequestParam(value = "searchParams") String searchParams)
    @Test // @GetMapping("/searchList")
    public void getSearchIndexerResults_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute + "/searchIndexer")
                        .param("q", "lu 7")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.positions").isArray())
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-searchResults_Indexer"));
    }


    /*------------------------- getPositionPublicPosting -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getPositionPublicPosting(
    //          @RequestParam(value = "positionId", required = true) String positionId)

    @Test // @GetMapping
    public void getPublicPosting_Valid() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .param("positionId", "67c3c01ab87e185493ae9c10")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.position").isNotEmpty())
                .andExpect(jsonPath("$.payload.position.positionId").value("67c3c01ab87e185493ae9c10"))
                .andDo(RestDocsConfig.getDefaultDocHandler("position-get-posting-by-id"));
    }

    @Test // @GetMapping
    public void getPublicPosting_ResourceNotFound() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .param("positionId", "111111111111111111111111")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.errMsg").value("Unable to process your request at this time"));
    }

    @Test // @GetMapping("/single")
    public void getPublicPosting_MissingParam() throws Exception {
        mockMvc.perform(get(positionControllerRoute)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: positionId"));
    }

    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)



}