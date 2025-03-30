package org.gatorapps.garesearch.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
@Document(collection = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class User {

    @Id
    private String id;

    @NotBlank(message = "User opid is required")
    @Field("opid")
    @Indexed(unique = true)
    private String opid;

    @NotNull(message = "User registerTimestamp is required")
    @Field("registerTimestamp")
    private Date registerTimestamp;

    @NotNull(message = "User roles are required")
    @Field("roles")
    private List<Integer> roles;

    @NotBlank(message = "User firstName is required")
    @Field("firstName")
    private String firstName;

    @NotBlank(message = "User lastName is required")
    @Field("lastName")
    private String lastName;

    @Field("nickname")
    private String nickname;

    @Field("emails")
    private List<String> emails;

    @Field("sessions")
    private List<Session> sessions;

    // Nested Session class
    @Getter
    @Setter
    public static class Session {
        @Field("sessionID")
        private String sessionID;

        @Field("signInTimeStamp")
        private Date signInTimeStamp;
    }
}
