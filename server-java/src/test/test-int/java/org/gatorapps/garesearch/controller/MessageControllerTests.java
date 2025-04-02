package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.config.RestDocsConfig;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.gatorapps.garesearch.repository.garesearch.MessageRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;

import static org.gatorapps.garesearch.constants.RequestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureRestDocs(outputDir="target/generated-snippets")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageControllerTests extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MessageRepository messageRepository;

    private final String messageControllerRoute = "/appApi/garesearch/message";

    /*------------------------- getMessageList -------------------------*/

    // @GetMapping("/list")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
    //            HttpServletRequest request,
    //            @RequestParam(value = "page", required = true) int page,
    //            @RequestParam(value = "size", required = true) int size
    //    )

    @Test // @GetMapping("/list")
    @Order(1)
    public void getMessageList_Valid() throws Exception {
        String response = mockMvc.perform(get(messageControllerRoute + "/list")
                        .param("page", "0")
                        .param("size", "5")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("msg-get-list"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/message/get_msg_list.json").toPath());

        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/list")
    @Order(1)
    public void getMessageList_MissingParamPage() throws Exception {
        mockMvc.perform(get(messageControllerRoute + "/list")
                        .param("size", "5")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: page"));
    }


    @Test // @GetMapping("/list")
    @Order(1)
    public void getMessageList_MissingParamSize() throws Exception {
        mockMvc.perform(get(messageControllerRoute + "/list")
                        .param("page", "0")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: size"));


    }


    /*------------------------- get single message -------------------------*/

    // @GetMapping("/single")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
    //            HttpServletRequest request,
    //            @RequestParam(value = "messageId", required = true) String messageId
    //    )

    @Test // @GetMapping("/single")
    @Order(2)
    public void getMessage_Valid() throws Exception {
        String response = mockMvc.perform(get(messageControllerRoute + "/single")
                        .param("messageId", "c1d335117621f49532e47b52")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andDo(RestDocsConfig.getDefaultDocHandler("msg-get-single"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/message/get_msg.json").toPath());

        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test // @GetMapping("/single")
    @Order(2)
    public void getMessage_ValidPermissionAsSender() throws Exception {
        String messageId = "a11335117621f49532e47b52";
        Message origMsg = messageRepository.findById(messageId).get();
        assertNotEquals(TEST_USER_OPID, origMsg.getRecipientOpid(), "test user is recipient. amend test data");
        assertEquals(TEST_USER_OPID, origMsg.getSenderOpid(), "test user was not sender. amend test data");
        boolean origStatus = origMsg.isRead();
        boolean isReadStatus = !origStatus;

        String response = mockMvc.perform(get(messageControllerRoute + "/single")
                        .param("messageId", messageId)
                        .param("isRead", String.valueOf(isReadStatus))
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = Files.readString(ResourceUtils.getFile("classpath:responses/message/get_msg_as_sender.json").toPath());

        JSONAssert.assertEquals(expectedResponse, response, false);
    }

    @Test //@GetMapping("/single")
    @Order(2)
    public void getMessage_InvalidPermission() throws Exception {
        String messageId = "a12135117621f49532e47b52";
        Message origMsg = messageRepository.findById(messageId).get();
        assertNotEquals(TEST_USER_OPID, origMsg.getRecipientOpid(), "test user is recipient. amend test data");
        assertNotEquals(TEST_USER_OPID, origMsg.getSenderOpid(), "test user is sender. amend test data");

        mockMvc.perform(get(messageControllerRoute + "/single")
                        .param("messageId", messageId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isForbidden()) // 401
                .andExpect(jsonPath("$.errCode").value("ERR_INVALID_ACCESS"))
                .andExpect(jsonPath("$.errMsg").value("Unauthorized to access message"));
    }



    @Test // @GetMapping("/list")
    @Order(2)
    public void getMessage_MissingParam() throws Exception {
        mockMvc.perform(get(messageControllerRoute + "/single")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: messageId"));
    }



    /*------------------------- update read status -------------------------*/

    // @GetMapping("/readStatus")
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
    //            HttpServletRequest request,
    //            @RequestParam(value = "messageId", required = true) String messageId,
    //            @RequestParam(value = "isRead", required = true) boolean isRead
    //    )

    @Test // @GetMapping("/readStatus")
    @Order(3)
    public void updateReadStaus_Valid() throws Exception {
        String messageId = "c1d335117621f49532e47b52";
        Message origMsg = messageRepository.findById(messageId).get();
        boolean origStatus = origMsg.isRead();
        boolean isReadStatus = !origStatus;

        mockMvc.perform(get(messageControllerRoute + "/readStatus")
                        .param("messageId", messageId)
                        .param("isRead", String.valueOf(isReadStatus))
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.payload").isEmpty())
                .andDo(RestDocsConfig.getDefaultDocHandler("msg-put-read-status"));


        // ensure isRead flipped
        Message msg = messageRepository.findById(messageId).get();
        assertNotEquals(origStatus, msg.isRead(), "Message status not changed");
    }


    @Test // @GetMapping("/single")
    @Order(3)
    public void updateReadStaus_InvalidPermission() throws Exception {
        String messageId = "a12135117621f49532e47b52";
        Message origMsg = messageRepository.findById(messageId).get();
        assertNotEquals(TEST_USER_OPID, origMsg.getRecipientOpid(), "test user is recipient. amend test data");
        boolean origStatus = origMsg.isRead();
        boolean isReadStatus = !origStatus;

        mockMvc.perform(get(messageControllerRoute + "/readStatus")
                        .param("messageId", messageId)
                        .param("isRead", String.valueOf(isReadStatus))
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isForbidden()) // 403
                .andExpect(jsonPath("$.errCode").value("ERR_INVALID_ACCESS"))
                .andExpect(jsonPath("$.errMsg").value("Unauthorized to access message"));


        // ensure isRead not modified
        Message msg = messageRepository.findById(messageId).get();
        assertEquals(origStatus, msg.isRead(), "Message status was changed when it shouldnt have");
    }

    @Test // @GetMapping("/single")
    @Order(3)
    public void updateReadStaus_InvalidPermissionAsSender() throws Exception {
        String messageId = "a11335117621f49532e47b52";
        Message origMsg = messageRepository.findById(messageId).get();
        assertNotEquals(TEST_USER_OPID, origMsg.getRecipientOpid(), "test user is recipient. amend test data");
        assertEquals(TEST_USER_OPID, origMsg.getSenderOpid(), "test user was not sender. amend test data");
        boolean origStatus = origMsg.isRead();
        boolean isReadStatus = !origStatus;

        mockMvc.perform(get(messageControllerRoute + "/readStatus")
                        .param("messageId", messageId)
                        .param("isRead", String.valueOf(isReadStatus))
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isForbidden()) // 403
                .andExpect(jsonPath("$.errCode").value("ERR_INVALID_ACCESS"))
                .andExpect(jsonPath("$.errMsg").value("Unauthorized to access message"));


        // ensure isRead not modified
        Message msg = messageRepository.findById(messageId).get();
        assertEquals(origStatus, msg.isRead(), "Message status was changed when it shouldnt have");
    }


    @Test // @GetMapping("/list")
    @Order(1)
    public void updateMessage_MissingParamMessageId() throws Exception {
        mockMvc.perform(get(messageControllerRoute + "/readStatus")
                        .param("isRead", String.valueOf(true))
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: messageId"));
    }


    @Test // @GetMapping("/list")
    @Order(1)
    public void updateReadStatus_MissingParamIsRead() throws Exception {
        String messageId = "c1d335117621f49532e47b52";
        mockMvc.perform(get(messageControllerRoute + "/readStatus")
                        .param("messageId", messageId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: isRead"));
    }


    /*------------------------- get message page -------------------------*/

    // @GetMapping("/messagePage")
    //    public ResponseEntity<ApiResponse<Map<String, Integer>>> getMessagePage(
    //            HttpServletRequest request,
    //            @RequestParam String messageId,
    //            @RequestParam int size
    //    )

    @Test // @GetMapping("/messagePage")
    @Order(4)
    public void getMessagePage_Valid() throws Exception {
        String messageId = "c3d335117621f49532e47b52";

        mockMvc.perform(get(messageControllerRoute + "/messagePage")
                        .param("messageId", messageId)
                        .param("size", "5")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value("0"))
                .andExpect(jsonPath("$.payload.page").value("0"))
                .andDo(RestDocsConfig.getDefaultDocHandler("msg-get-page"));

    }


    @Test // @GetMapping("/list")
    @Order(1)
    public void getMessagePage_MissingParamMessageId() throws Exception {
        mockMvc.perform(get(messageControllerRoute + "/messagePage")
                        .param("size", "5")
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: messageId"));
    }


    @Test // @GetMapping("/list")
    @Order(1)
    public void getMessagePage_MissingParamSize() throws Exception {
        String messageId = "c3d335117621f49532e47b52";

        mockMvc.perform(get(messageControllerRoute + "/messagePage")
                        .param("messageId", messageId)
                        .header(HEADER_NAME, VALID_HEADER_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, VALID_COOKIE_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.errCode").value("ERR_REQ_MISSING_REQUIRED_PARAM"))
                .andExpect(jsonPath("$.errMsg").value("Missing required req params: size"));
    }



    /*------------------------- controller function -------------------------*/
    // @_____Mapping
    //    public ResponseEntity<ApiResponse<Map<String, Object>>> functionName(
    //          @RequestParam(value = "example", required = true) String example)


}
