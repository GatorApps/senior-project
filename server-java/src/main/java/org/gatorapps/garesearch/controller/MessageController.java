package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.gatorapps.garesearch.dto.ApiResponse;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.gatorapps.garesearch.service.MessageService;
import org.gatorapps.garesearch.utils.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appApi/garesearch/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserAuthUtil userAuthUtil;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
            HttpServletRequest request,
            @RequestParam(value = "page", required = true) int page,
            @RequestParam(value = "size", required = true) int size
    ) throws Exception {
        List<Message> messages = messageService.getUserMessages(userAuthUtil.retrieveOpid(request), page, size);

        Map<String, Object> payloadResponse = Map.of(
                "messages", messages);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/single")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
            HttpServletRequest request,
            @RequestParam(value = "messageId", required = true) String messageId
    ) throws Exception {
        Message message = messageService.getMessage(messageId);

        // Check user has access to message
        if (!(message.getRecipientOpid().equals(userAuthUtil.retrieveOpid(request)) ||
                message.getSenderOpid().equals(userAuthUtil.retrieveOpid(request)))) {
            throw new RuntimeException("Unauthorized to access message");
        }

        Map<String, Object> payloadResponse = Map.of(
                "message", message);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0", payloadResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/readStatus")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageList(
            HttpServletRequest request,
            @RequestParam(value = "messageId", required = true) String messageId,
            @RequestParam(value = "isRead", required = true) boolean isRead
    ) throws Exception {
        Message message = messageService.getMessage(messageId);

        // Check user is recipient of message
        if (!message.getRecipientOpid().equals(userAuthUtil.retrieveOpid(request))) {
            throw new RuntimeException("Unauthorized to access message");
        }

        messageService.setIsRead(messageId, isRead);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>("0");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
