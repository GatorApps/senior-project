package org.gatorapps.garesearch.model.garesearch;

import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.validators.OpidExists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@Document(collection = "applicantprofiles")
public class ApplicantProfile extends BaseApplicationProfileSchema {

    @Id
    private String id;

    @Field("opid")
    @NotBlank(message = "User opid is required")
    @Indexed(unique=true)
    @OpidExists // note: must use @Valid annotation when writing ApplicantProfile profile in controller for example
    private String opid;

    @Field("lastUpdateTimeStamp")
    @NotBlank(message = "lastUpdateTimeStamp is required")
    private Date lastUpdateTimeStamp;
}
