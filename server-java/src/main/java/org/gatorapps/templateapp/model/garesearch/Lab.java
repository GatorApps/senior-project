package org.gatorapps.templateapp.model.garesearch;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.gatorapps.templateapp.validators.OpidExists;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Document(collection = "labs")
public class Lab {

    @Id
    private String id;

    @Field("users")
    private List<User> users;

    @Field("name")
    @NotBlank(message = "Lab name is required")
    private String name;

    @Field("department")
    private String department;

    @Field("website")
    @URL(message = "Links must start with https:// or http:// and have a valid url format")
    private String website;

    @Field("email")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Field("description")
    private String description;

    public static class User {
        @Field("opid")
        @NotBlank(message = "User opid is required")
        @Indexed(unique=true)
        @OpidExists
        private String opid;

        @Field("role")
        @NotBlank(message = "Lab user role is required")
        private String role;
    }
}
