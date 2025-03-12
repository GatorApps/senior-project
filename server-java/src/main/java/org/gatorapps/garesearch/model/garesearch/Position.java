package org.gatorapps.garesearch.model.garesearch;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import org.gatorapps.garesearch.validators.LabIdExists;
import org.jsoup.Jsoup;
import org.springframework.data.annotation.CreatedDate;
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

    @Field("rawDescription")
    private String rawDescription;

    @Field("postedTimeStamp")
    @NotNull(message = "Posted timestamp is required")
    private Date postedTimeStamp;

    @Field("lastUpdatedTimeStamp")
    @NotNull(message = "Last updated timestamp is required")
    private Date lastUpdatedTimeStamp;

    @Field("status")
    @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'")
    private String status;

    @Field("positionSpecificSupplements")
    private String positionSpecificSupplements;

    public void setDescription(String description) {
        this.description = description;
        this.rawDescription = Jsoup.parse(description).text();
    }

    public void setRawDescription(String description) {
        this.rawDescription = Jsoup.parse(description).text();
    }

    public void setPostedTimeStamp(){
        this.postedTimeStamp = new Date();
    }
}
