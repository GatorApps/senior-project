package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.repository.garesearch.FileRepository;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;


import java.nio.file.Files;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicantControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    private final String applicantControllerRoute = "/appApi/garesearch/applicant";


    @Autowired
    private FileRepository fileRepository;


    /*------------------------- getApplicantResumeMetadata -------------------------*/

    // @GetMapping("/resumeMetadata")
    //    public ResponseEntity<?> getApplicantResumeMetadata(@Valid HttpServletRequest request)

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getResumeMetadata_Valid() throws Exception {
        String response = mockMvc.perform(get(applicantControllerRoute+"/resumeMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-resume-metadata-get"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/applicant/get_resume_metadata.json").toPath());

        response = response.replaceAll("\"uploadedTimeStamp\":\\d+", "\"uploadedTimeStamp\": \"<timestamp>\"");

        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getResumeMetadata_Invalid() throws Exception {
        mockMvc.perform(get(applicantControllerRoute + "/resumeMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer 4cfc3a9b-66ac-4a84-937c-3ffb4bebbd25"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errCode").value("APPLICANT_NO_RESUME_UPLOADED"))
                .andExpect(jsonPath("$.errMsg").value("No resume uploaded"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @GetMapping("/resumeMetadata")
    @Order(1)
    public void getResumeMetadata_InvalidId() throws Exception {
        mockMvc.perform(get(applicantControllerRoute + "/resumeMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer 4cdc3a9b-66ac-4a84-937c-3ffb4bebbd25"))
                .andDo(print())
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid resumeId linked to applicant profile"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }



    /*------------------------- getApplicantTranscriptMetadata -------------------------*/

    // @GetMapping("/transcriptMetadata")
    //    public ResponseEntity<?> getApplicantTranscriptMetadata(@Valid HttpServletRequest request)

    @Test // @GetMapping("/transcriptMetadata")
    @Order(2)
    public void getTranscriptMetadata_Valid() throws Exception {
        String response = mockMvc.perform((get(applicantControllerRoute + "/transcriptMetadata")
                .header(HEADER_NAME, VALID_HEADER_VALUE)
                .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-transcript-metadata-get"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/applicant/get_transcript_metadata.json").toPath());

        response = response.replaceAll("\"uploadedTimeStamp\":\\d+", "\"uploadedTimeStamp\": \"<timestamp>\"");

        JSONAssert.assertEquals(expectedResponse, response, false);
    }


    @Test // @GetMapping("/transcriptMetadata")
    @Order(2)
    public void getTranscriptMetadata_Invalid() throws Exception {
        mockMvc.perform(get(applicantControllerRoute + "/transcriptMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer 4cfc3a9b-66ac-4a84-937c-3ffb4bebbd25"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errCode").value("APPLICANT_NO_TRANSCRIPT_UPLOADED"))
                .andExpect(jsonPath("$.errMsg").value("No transcript uploaded"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test // @GetMapping("/transcriptMetadata")
    @Order(2)
    public void getTranscriptMetadata_InvalidId() throws Exception {
        mockMvc.perform(get(applicantControllerRoute + "/transcriptMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer 4cdc3a9b-66ac-4a84-937c-3ffb4bebbd25"))
                .andDo(print())
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.errCode").value("-"))
                .andExpect(jsonPath("$.errMsg").value("Invalid transcriptId linked to applicant profile"))
                .andExpect(jsonPath("$.payload").isEmpty());
    }


    /*------------------------- uploadApplicantResume / uploadApplicantTranscript -------------------------*/
    // they use the same logic (service function)

    // @PostMapping("/resume")
    //    public ResponseEntity<?> uploadApplicantResume(@Valid HttpServletRequest request,
    //          @RequestParam("resume") MultipartFile file)


    // @PostMapping("/transcript")
    //    public ResponseEntity<?> uploadApplicantTranscript(@Valid HttpServletRequest request,
    //          @RequestParam("transcript") MultipartFile file)

    @Test // @PostMapping("/resume")
    @Order(3)
    public void postResume_Valid() throws Exception {
        String fileName = "test_resume_upload.pdf";
        String filepath = "/data/test.pdf";

        MockMultipartFile file = new MockMultipartFile("resume", fileName, "application/pdf", new ClassPathResource(filepath).getInputStream());

        mockMvc.perform(multipart(applicantControllerRoute + "/resume")
                        .file(file)

                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-resume-post"));

        // file was added to File collection in database successfully
        assertTrue(fileRepository.existsByName(fileName), "File not uploaded to database successfully");
    }

    @Test // @PostMapping("/resume")
    @Order(4)
    public void postTranscript_Valid() throws Exception {
        String fileName = "test_transcript_upload.pdf";
        String filepath = "/data/test.pdf";

        MockMultipartFile file = new MockMultipartFile("transcript", fileName, "application/pdf", new ClassPathResource(filepath).getInputStream());

        mockMvc.perform(multipart(applicantControllerRoute + "/transcript")
                        .file(file)

                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-transcript-post"));

        // file was added to File collection in database successfully
        assertTrue(fileRepository.existsByName(fileName), "File not uploaded to database successfully");
    }


    // Following exceptions handled in service, which is shared, therefore exception checking for one route is sufficient
    @Test // @PostMapping("/resume")
    @Order(5)
    public void postResume_null() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resume", "test_resume.pdf", "application/pdf", (byte[]) null);

        String response = mockMvc.perform(multipart(applicantControllerRoute + "/resume")
                        .file(file)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();


        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/file_not_provided.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @PostMapping("/resume")
    @Order(5)
    public void postResume_maxFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resume", "test_resume.pdf", "application/pdf", new byte[6242880]);

        String response = mockMvc.perform(multipart(applicantControllerRoute + "/resume")
                        .file(file)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/file_max_size.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @PostMapping("/resume")
    @Order(5)
    public void postResume_invalidType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resume", "test_resume.txt", "application/pdf", new byte[5]);

        String response = mockMvc.perform(multipart(applicantControllerRoute + "/resume")
                        .file(file)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/exceptions/file_invalid_type.json").toPath());
        JSONAssert.assertEquals(expectedResponse, response, false);
    }

}
