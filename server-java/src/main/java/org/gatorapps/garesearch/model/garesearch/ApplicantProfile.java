package org.gatorapps.garesearch.model.garesearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.BaseApplicationProfileSchema;
import org.gatorapps.garesearch.validators.OpidExists;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

@Getter
@Setter
@Document(collection = "applicantprofiles")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantProfile extends BaseApplicationProfileSchema {

    @Id
    private String id;

    @Field("opid")
    @NotBlank(message = "User opid is required")
    @Indexed(unique=true)
    @OpidExists // note: must use @Valid annotation when writing ApplicantProfile profile in controller for example
    private String opid;

    @LastModifiedDate
    @Field("lastUpdatedTimeStamp")
    @NotNull(message = "lastUpdatedTimeStamp is required")
    private Date lastUpdatedTimeStamp;
}
