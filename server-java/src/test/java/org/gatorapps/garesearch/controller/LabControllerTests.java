package org.gatorapps.garesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.util.Map;
import java.util.Random;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// TODO : check the database for get, create, and update to ensure actually gets the correct get / updated / created

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class LabControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    private final String labControllerRoute = "/appApi/garesearch/lab";

    /*------------------------- getLabPublicProfile -------------------------*/

    // @GetMapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabPublicProfile(
    //          @RequestParam(value = "labId", required = true) String labId)

    @Test // @GetMapping
    public void getPublicProfile_Valid() throws Exception {
        String response = mockMvc.perform(get(labControllerRoute)
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-get-by-id"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/lab/get_public_lab.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
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


    /*------------------------- getLabsNamesList -------------------------*/
    // @GetMapping("/labsList")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabsNamesList(HttpServletRequest request)

    @Test // @GetMapping("/labsList")
    public void getLabsNamesList_Valid() throws Exception {
        String response = mockMvc.perform(get(labControllerRoute + "/labsList")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-get-names-list"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/lab/get_labs_names_list.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    /*------------------------- getLabProfile -------------------------*/
    // @GetMapping("/profileEditor")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getLabProfile(@Valid HttpServletRequest request,
    //          @RequestParam(value="labId") String labId) throws Exception {

    @Test // @GetMapping("/profileEditor")
    public void getProfile_Valid() throws Exception {
        String response = mockMvc.perform(get(labControllerRoute + "/profileEditor")
                        .param("labId", "88dcf5a77621f49532e47b52")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-faculty-get-by-id"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/lab/get_lab_editor.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/profileEditor")
    public void getProfile_InvalidLabAccess() throws Exception {
        String response = mockMvc.perform(get(labControllerRoute  + "/profileEditor")
                        .param("labId", "99dcf5a77621f49532e47b52")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/profileEditor")
    public void getProfile_MissingParam() throws Exception {
        mockMvc.perform(get(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: labId"));
    }



    /*------------------------- createLabProfile -------------------------*/
    // @PostMapping("/profileEditor")
    //    public ResponseEntity<ApiResponse<Void>> createLabProfile(@Valid HttpServletRequest request,
    //          @Valid @RequestBody Lab lab) throws Exception {

    @Test // @PostMapping("/profileEditor")
    public void createNewLab_Valid() throws Exception {
        String requestBody = String.format("""
                    {
                        "name": "Ava's New Test Lab %d",
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
    public void createNewLab_invalid() throws Exception {
        String requestBody = String.format("""
                    {
                        "name": "",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """);
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



    /*------------------------- updateLabProfile -------------------------*/
    // @PutMapping("/profileEditor")
    //    public ResponseEntity<ApiResponse<Void>> updateLabProfile(@Valid HttpServletRequest request,
    //          @RequestBody Lab lab) throws Exception {

    @Test // @PutMapping("/profileEditor")
    public void updateLab_Valid() throws Exception {
        String requestBody = String.format("""
                    {   
                        "id": "e7dd015e481a0b5b0bccbaa7",
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
        mockMvc.perform(put(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("lab-update-existing"));
    }


    @Test // @PutMapping("/profileEditor")
    public void updateLab_InvalidLabAccess() throws Exception {
        String requestBody = String.format("""
                    {
                        "id": "67dd015e481a0b5b0bccbaa7",
                        "name": "test lab",
                        "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing",
                        "email": "testEmail@gmail.com",
                        "website": "https://testlab.gatorapps.org"
                    }
                    """);
        String response = mockMvc.perform(put(labControllerRoute + "/profileEditor")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/invalid_lab_access.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }



    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)


}
