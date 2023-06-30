package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import lombok.Getter;
import lombok.Setter;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Education;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Experience;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Link;
import org.gatorapps.garesearch.model.garesearch.supportingclasses.Project;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/** JSONs in lists need to declared in separate files or serializable issues arise **/
@Getter
@Setter
public class BaseApplicationProfileSchema {
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
}
