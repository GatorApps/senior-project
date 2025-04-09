package org.gatorapps.garesearch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ApplicationWithUserInfo {
    private String applicationId;
    private String opid;
    private String positionId;
    private String resumeId;
    private String transcriptId;
    private String supplementalResponses;
    private Date submissionTimeStamp;
    private String status;

    private String firstName;
    private String lastName;
    private String email;
}
