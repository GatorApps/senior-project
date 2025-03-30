package org.gatorapps.garesearch.model.global;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Document(collection = "sessions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {

    // Getters and Setters for Session
    @Getter
    @NotBlank(message = "Session ID is required")
    @Id
    private String id;

    @Getter
    @Field("expires")
    private Date expires;

    @Getter
    @Field("session")
    private String session;

}
