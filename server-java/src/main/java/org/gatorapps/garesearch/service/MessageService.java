package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.garesearch.Message;
import org.gatorapps.garesearch.repository.garesearch.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SESService sesService;

    public void sendMessage(String senderOpid, String recipientOpid, String title, String content) {
        // Create and save new message
        Message message = new Message();
        message.setSenderOpid(senderOpid);
        message.setRecipientOpid(recipientOpid);
        message.setTitle(title);
        message.setContent(content);
        messageRepository.save(message);

        // Send email notification
        sesService.sendEmailToOpid(recipientOpid, title, content);
    }

//    public List<Message> getUserMessages(String recipientOpid) {
//        return messageRepository.findByRecipientOpidOrderByCreatedAtDesc(recipientOpid);
//    }

    public void markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }
}
