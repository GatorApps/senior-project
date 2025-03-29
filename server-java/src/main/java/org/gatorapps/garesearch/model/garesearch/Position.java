package org.gatorapps.garesearch.model.garesearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import org.gatorapps.garesearch.validators.LabIdExists;
import org.jsoup.Jsoup;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;


@Getter
@Setter
@Document(collection = "positions")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @CreatedDate
    private Date postedTimeStamp;

    @Field("lastUpdatedTimeStamp")
    @LastModifiedDate // will update automatically whenever entity is updated
    private Date lastUpdatedTimeStamp;

    @Field("status")
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "open|closed|archived", message = "Position status must be one of 'open', 'closed', 'archived'")
    private String status;

    @Field("supplementalQuestions")
    private String supplementalQuestions;

    public void setDescription(String description) {
        this.description = description;
        this.rawDescription = Jsoup.parse(description).text();
    }

    public void setRawDescription(String description) {
        this.rawDescription = Jsoup.parse(description).text();
    }
}
