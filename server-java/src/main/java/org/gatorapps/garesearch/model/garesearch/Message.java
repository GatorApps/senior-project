package org.gatorapps.garesearch.model.garesearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Document(collection = "messages")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message{

    @Id
    private String id;

    private String senderOpid;

    @NotBlank(message = "recipientOpid is required")
    private String recipientOpid;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    @CreatedDate
    @Field("sentTimeStamp")
    @NotNull(message = "sentTimeStamp is required")
    private Date sentTimeStamp;

    @Field("isRead")
    @NotNull(message = "isRead is required")
    private boolean isRead;

}