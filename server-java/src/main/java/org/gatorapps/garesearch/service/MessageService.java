package org.gatorapps.garesearch.service;

import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.gatorapps.garesearch.repository.garesearch.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SESService sesService;

    @Autowired
    private UserService userService;

    @Value("${app.frontend-host}")
    private String frontendHost;

    public void sendMessage(String senderOpid, String recipientOpid, String title, String content) {
        // Validate recipient
        Optional<User> recipientOptional = userService.getUserByOpid(recipientOpid);
        if (recipientOptional.isEmpty()) {
            throw new RuntimeException("Cannot send message: recipient not found");
        }
        User recipient = recipientOptional.get();

        // Create and save new message
        Message message = new Message();
        message.setSenderOpid(senderOpid);
        message.setRecipientOpid(recipientOpid);
        message.setTitle(title);
        message.setContent(content);
        messageRepository.save(message);

        // Send email notification
        List<Map<String, String>> links = List.of(
                Map.of("url", String.format("%s/messages?messageId=%s", frontendHost, message.getId()),
                        "text", "View message in Message Center"),
                Map.of("url", String.format("%s/settings#notifications", frontendHost),
                        "text", "Update your notification settings")
        );
        sesService.sendBrandedEmail(recipient.getEmails().get(0), title, content, links);
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
