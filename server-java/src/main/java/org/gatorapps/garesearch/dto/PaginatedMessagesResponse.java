package org.gatorapps.garesearch.dto;

import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;

@Getter
@Setter
public class PaginatedMessagesResponse {
    private List<MessagePreview> messages;
    private int currentPage;
    private int totalPages;
    private long totalMessages;

    public PaginatedMessagesResponse(Page<Message> messagePage) {
        this.messages = messagePage.getContent().stream().map(MessagePreview::new).collect(Collectors.toList());
        this.currentPage = messagePage.getNumber();
        this.totalPages = messagePage.getTotalPages();
        this.totalMessages = messagePage.getTotalElements();
    }
}

@Getter
@Setter
class MessagePreview {
    private String id;
    private String senderOpid;
    private String recipientOpid;
    private String title;
    private String contentPreview;
    private Date sentTimeStamp;
    private boolean isRead;

    public MessagePreview(Message message) {
        this.id = message.getId();
        this.senderOpid = message.getSenderOpid();
        this.recipientOpid = message.getRecipientOpid();
        this.title = message.getTitle();
        this.contentPreview = generatePreview(message.getContent());
        this.sentTimeStamp = message.getSentTimeStamp();
        this.isRead = message.isRead();
    }

    private String generatePreview(String htmlContent) {
        String textContent = Jsoup.parse(htmlContent).text(); // Remove HTML tags
        return textContent.length() > 100 ? textContent.substring(0, 100) + "..." : textContent;
    }
}
