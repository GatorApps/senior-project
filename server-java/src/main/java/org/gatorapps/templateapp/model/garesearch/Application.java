package org.gatorapps.templateapp.model.garesearch;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.templateapp.validators.OpidExists;
import org.gatorapps.templateapp.validators.PositionIdExists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "applications")
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

    @Field("submissionTimeStamp")
    @NotBlank(message = "submissionTimeStamp is required")
    private Date submissionTimeStamp;

    @Field("status")
    @NotBlank(message = "status is required")
    @Pattern(regexp = "draft|open|closed", message = "Position status must be one of 'draft', 'open', 'closed'")
    private String status;
}
