package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileControllerTests extends BaseTest {
    @Autowired
    private MockMvc mockMvc;

    private final String fileControllerRoute = "/appApi/garesearch/file";


    /*------------------------- getApplicantResumeMetadata -------------------------*/

    // @GetMapping("/{fileId}")
    //    public ResponseEntity<?> getApplicantResumeMetadata(
    //    @Valid HttpServletRequest request,
    //    @PathVariable String fileId)

    // TODO : the 2 valid tests once controller is complete.
    //  will need to upload file into s3 and File collection before running

    /*

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getFile_AsUploader_Valid() throws Exception {
        String fileId = "111111111111111111111111";
        mockMvc.perform(get(fileControllerRoute + "/" + fileId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("file-get-resume"));
    }

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getFile_AsAuthorizedViewer_Valid() throws Exception {
        String fileId = "111111111111111111111111";
        mockMvc.perform(get(fileControllerRoute + "/" + fileId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

     */

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getFile_NotInS3() throws Exception {
        String fileId = "1a1cf5a77621f49532e47b52";
        mockMvc.perform(get(fileControllerRoute + "/" + fileId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Unable to download file"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getFile_NotFound() throws Exception {
        String fileId = "111111111111111111111111";
        mockMvc.perform(get(fileControllerRoute + "/" + fileId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())

                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid fileId"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }


}
