package org.gatorapps.garesearch.model.garesearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.BaseApplicationProfileSchema;
import org.gatorapps.garesearch.validators.OpidExists;
import org.gatorapps.garesearch.validators.PositionIdExists;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


@Getter
@Setter
@Document(collection = "applications")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends BaseApplicationProfileSchema {

    @Id
    private String id;

    @Field("opid")
    @NotBlank(message = "User opid is required")
    @OpidExists // note: must use @Valid annotation when writing Application application in controller for example
    private String opid;

    @Field("positionId")
    @NotBlank(message = "positionId is required")
    @PositionIdExists
    private String positionId;

    @CreatedDate
    @Field("submissionTimeStamp")
    @NotNull(message = "submissionTimeStamp is required")
    private Date submissionTimeStamp;

    @Field("status")
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "submitted|archived|moving forward", message = "Application status must be one of 'submitted', 'archived', or 'moving forward'")
    private String status;

    @Field("supplementalResponses")
    private String supplementalResponses;
}
