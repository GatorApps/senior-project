package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

/** JSONs in lists need to declared in separate files or serializable issues arise **/
@Getter
@Setter
public class BaseApplicationProfileSchema {
    @Field("resumeId")
    private String resumeId;

    @Field("transcriptId")
    private String transcriptId;
}
