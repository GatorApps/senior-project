package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class Link {
    public Link(){};

    @Field("title")
    private String title;

    @Field("url")
    @URL(message = "Links must start with https:// or http:// and have a valid url format")
    private String url;
}

