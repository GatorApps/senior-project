package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.validators.EndDateValid;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Getter
@Setter
public class Project {
    public Project(){};

    @Field("name")
    @NotBlank(message = "Project name is required")
    private String name;

    @Field("startDate")
    @NotBlank(message = "startDate is required")
    private Date startDate;

    @Field("endDate")
    @EndDateValid
    private Date endDate;

    @Field("description")
    private String description;
}
