package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    /*------------------------- Get labsList -------------------------*/
    // @GetMapping("/labsList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabsList(HttpServletRequest request)

    @Test // @GetMapping("/labsList")
    public void getLabsList_Valid() throws Exception {
        mockMvc.perform(get(labControllerRoute + "/labsList")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload.labs").exists())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-get-list"));
    }


    /*------------------------- saveLab -------------------------*/
    // @PostMapping("/profileEditor")
    //    public ResponseEntity<ApiResponse<Void>> saveLabProfile(
    //          @Valid HttpServletRequest request,
    //          @Valid @RequestBody Lab lab)

    @Test // @PostMapping("/profileEditor")
    public void createNewLab_Valid() throws Exception {
        String requestBody = String.format("""
                    {
                        "name": "Ava's Test Lab %d",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-create-new"));
    }

    @Test // @PostMapping("/profileEditor")
    public void updateLab_Valid() throws Exception {
        String requestBody = String.format("""
                    {   
                        "id": "67d5cc4dfd43841f90c4d7cb",
                        "users": [
                            {
                                "opid": "%s",
                                "role": "admin"
                            }
                        ],
                        "name": "Ava's Updated Lab %d",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """, TEST_USER_OPID, new Random().nextInt(1000));
        mockMvc.perform(post(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-update-existing"));
    }


    @Test // @PostMapping("/profileEditor")
    public void createNewLab_invalid() throws Exception {
        String requestBody = String.format("""
                    {
                        "name": "",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_INPUT_FAIL_VALIDATION"))
                .andExpect(jsonPath("$.payload.name").isNotEmpty());
    }

    @Test // @PostMapping("/profileEditor")
    public void updateNewLab_invalidLabAccess() throws Exception {
        String requestBody = String.format("""
                    {
                        "id": "67d5c0f411bf542d9f56f648",
                        "name": "test lab",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """, new Random().nextInt(1000));
        mockMvc.perform(post(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden()) // 404
                .andExpect(jsonPath("$.errCode").value("ERR_INVALID_ACCESS"))
                .andExpect(jsonPath("$.errMsg").isNotEmpty());
    }




    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)


}
