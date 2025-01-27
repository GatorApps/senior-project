package org.gatorapps.garesearch.model.garesearch;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.validators.LabIdExists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;


@Getter
@Setter
@Document(collection = "positions")
public class Position {

    @Id
    private String id;

    @Field("labId")
    @NotBlank(message = "Lab ID is required")
    @LabIdExists
    private String labId;

    @Field("name")
    @NotBlank(message = "Position name is required")
    private String name;

    @Field("description")
    private String description;

    @Field("postedTimeStamp")
    @NotNull(message = "Posted timestamp is required")
    private Date postedTimeStamp;

    @Field("status")
    @Pattern(regexp = "draft|open|closed", message = "Position status must be one of 'draft', 'open', 'closed'")
    private String status;
}
