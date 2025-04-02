package org.gatorapps.garesearch.dto;

import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.Message;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Setter
public class PaginatedMessagesResponse {
    private List<Message> messages;
    private int currentPage;
    private int totalPages;
    private long totalMessages;

    public PaginatedMessagesResponse(Page<Message> messagePage) {
        this.messages = messagePage.getContent();
        this.currentPage = messagePage.getNumber();
        this.totalPages = messagePage.getTotalPages();
        this.totalMessages = messagePage.getTotalElements();
    }
}
