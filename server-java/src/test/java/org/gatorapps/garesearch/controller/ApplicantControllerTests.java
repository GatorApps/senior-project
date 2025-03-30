package org.gatorapps.garesearch.controller;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.gatorapps.garesearch.config.RestDocsConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO : s3 mocking
// TODO : invalid tests

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
public class ApplicantControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    private final String applicantControllerRoute = "/appApi/garesearch/applicant";

    @Autowired
    private S3Client s3Client;

    void createBucket(){
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket("testbucket").build();
        s3Client.createBucket(bucketRequest);
    }




    /*------------------------- getApplicantResumeMetadata -------------------------*/

    // @GetMapping("/resumeMetadata")
    //    public ResponseEntity<?> getApplicantResumeMetadata(@Valid HttpServletRequest request)

    @Test // @GetMapping("/resumeMetadata")
    public void getResumeMetadata_Valid() throws Exception {
        mockMvc.perform(get(applicantControllerRoute+"/resumeMetadata")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload.resumeMetadata").exists())
                .andExpect(jsonPath("$.payload.resumeMetadata.fileId").exists())
                .andExpect(jsonPath("$.payload.resumeMetadata.fileName").exists())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-resume-metadata-get"));
    }

//    @Test // @GetMapping("/resumeMetadata")
//    public void getResumeMetadata_Invalid() throws Exception {
//        mockMvc.perform(get(applicantControllerRoute + "/resumeMetadata")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, "someOtherOpidWithNoResume"))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errCode").value("0"))
//                .andExpect(jsonPath("$.payload.resumeMetadata").exists())
//                .andExpect(jsonPath("$.payload.resumeMetadata.fileId").exists())
//                .andExpect(jsonPath("$.payload.resumeMetadata.fileName").exists());
//    }



    /*------------------------- getApplicantTranscriptMetadata -------------------------*/

    // @GetMapping("/transcriptMetadata")
    //    public ResponseEntity<?> getApplicantTranscriptMetadata(@Valid HttpServletRequest request)

    @Test // @GetMapping("/transcriptMetadata")
    public void getTranscriptMetadata_Valid() throws Exception {
        mockMvc.perform((get(applicantControllerRoute + "/transcriptMetadata")
                .header(HEADER_NAME, VALID_HEADER_VALUE)
                .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload.resumeMetadata").exists())
                .andExpect(jsonPath("$.payload.resumeMetadata.fileId").exists())
                .andExpect(jsonPath("$.payload.resumeMetadata.fileName").exists())
                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-transcript-metadata-get"));
    }


//    @Test // @GetMapping("/resumeMetadata")
//    public void getResumeMetadata_Invalid() throws Exception {
//        mockMvc.perform(get(applicantControllerRoute + "/transcriptMetadata")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, "someOtherOpidWithNoResume"))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errCode").value("0"))
//                .andExpect(jsonPath("$.payload.resumeMetadata").exists())
//                .andExpect(jsonPath("$.payload.resumeMetadata.fileId").exists())
//                .andExpect(jsonPath("$.payload.resumeMetadata.fileName").exists());
//    }


    /*------------------------- getApplicantTranscriptMetadata -------------------------*/

    // @PostMapping("/resume")
    //    public ResponseEntity<?> uploadApplicantResume(@Valid HttpServletRequest request,
    //          @RequestParam("resume") MultipartFile file)

    @Test // @PostMapping("/resume")
    public void postResume_Valid() throws Exception {
        createBucket();

        String filepath = "/data/garesearch/test.pdf";

        MockMultipartFile file = new MockMultipartFile("resume", "test_resume.pdf", "application/pdf", new ClassPathResource(filepath).getInputStream());



        mockMvc.perform(multipart(applicantControllerRoute + "/resume")
                        .file(file)

                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

//        mockMvc.perform((post(applicantControllerRoute + "/resume")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-resume-post"));
    }


    // @PostMapping("/transcript")
    //    public ResponseEntity<?> uploadApplicantTranscript(@Valid HttpServletRequest request,
    //          @RequestParam("transcript") MultipartFile file)

//    @Test // @PostMapping("/resume")
//    public void postTranscript_Valid() throws Exception {
//        mockMvc.perform((post(applicantControllerRoute + "/transcript")
//                        .header(HEADER_NAME, VALID_HEADER_VALUE)
//                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(RestDocsConfig.getDefaultDocHandler("applicant-resume-post"));
//    }
}
