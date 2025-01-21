package org.gatorapps.templateapp.model.account;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Date;

@Document(collection = "user") // Collection name in the garesearch database
public class User {

    @Id
    private String id;

    @NotBlank(message = "User opid is required")
    @Field("opid")
    private String opid; // Reference to User (String or another entity)

    @NotBlank(message = "Position ID is required")
    @Field("positionId")
    private String positionId; // Reference to Position

    @NotNull(message = "Submission timestamp is required")
    @Field("submissionTimeStamp")
    private Date submissionTimeStamp;

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "saved|submitted|withdrawn|interview|accepted|denied|closed",
            message = "Application status must be one of 'saved', 'submitted', 'withdrawn', 'interview', 'accepted', 'denied', 'closed'"
    )
    @Field("status")
    private String status;

    // Getters and Setters
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

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Date getSubmissionTimeStamp() {
        return submissionTimeStamp;
    }

    public void setSubmissionTimeStamp(Date submissionTimeStamp) {
        this.submissionTimeStamp = submissionTimeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
