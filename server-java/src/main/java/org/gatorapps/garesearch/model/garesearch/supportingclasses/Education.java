package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.validators.EndDateValid;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
public class Education {
    public Education(){};

    @Field("institution")
    @NotBlank(message = "Institution name is required")
    private String institution;

    @Field("startDate")
    @NotNull(message = "startDate is required")
    private Date startDate;

    @Field("endDate")
    @EndDateValid
    private Date endDate;

    @Field("degree")
    private String degree;

    @Field("major")
    private String major;

    @Field("description")
    private String description;
}