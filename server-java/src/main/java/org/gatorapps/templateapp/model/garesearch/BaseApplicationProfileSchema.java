package org.gatorapps.templateapp.model.garesearch;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.templateapp.validators.EndDateValid;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection="baseapplicationprofiles")
public class BaseApplicationProfileSchema {
    // Getters and Setters
    @Field("education")
    private List<Education> education;

    @Field("experiences")
    private List<Experience> experiences;

    @Field("projects")
    private List<Project> projects;

    @Field("skills")
    private List<String> skills;

    @Field("links")
    private List<Link> links;

    @Field("additionalInformation")
    private String additionalInformation;

    public static class Education {
        @Field("institution")
        @NotBlank(message = "Institution name is required")
        private String institution;

        @Field("startDate")
        @NotBlank(message = "startDate is required")
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

    public static class Experience {
        @Field("employer")
        @NotBlank(message = "Employer name is required")
        private String employer;

        @Field("startDate")
        @NotBlank(message = "startDate is required")
        private Date startDate;

        @Field("endDate")
        @EndDateValid
        private Date endDate;

        @Field("title")
        private String title;

        @Field("description")
        private String description;
    }

    public static class Project {
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

    public static class Link {
        @Field("title")
        private String title;

        @Field("url")
        @URL(message = "Links must start with https:// or http:// and have a valid url format")
        private String url;
    }

}
