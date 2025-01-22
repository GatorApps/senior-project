package org.gatorapps.templateapp.model.account;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
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
    public static class Session {
        @Field("sessionID")
        private String sessionID;

        @Field("signInTimeStamp")
        private Date signInTimeStamp;

        // Getters and Setters for Session
        public String getSessionID() {
            return sessionID;
        }

        public void setSessionID(String sessionID) {
            this.sessionID = sessionID;
        }

        public Date getSignInTimeStamp() {
            return signInTimeStamp;
        }

        public void setSignInTimeStamp(Date signInTimeStamp) {
            this.signInTimeStamp = signInTimeStamp;
        }
    }

    // Getters and Setters for User
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public Date getRegisterTimestamp() {
        return registerTimestamp;
    }

    public void setRegisterTimestamp(Date registerTimestamp) {
        this.registerTimestamp = registerTimestamp;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}
