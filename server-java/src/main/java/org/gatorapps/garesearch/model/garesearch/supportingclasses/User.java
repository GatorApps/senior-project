package org.gatorapps.garesearch.model.garesearch.supportingclasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gatorapps.garesearch.validators.OpidExists;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Field("opid")
    @NotBlank(message = "User opid is required")
    @Indexed(unique=true)
    @OpidExists
    private String opid;

    @Field("role")
    @NotBlank(message = "Lab user role is required")
    private String role;
}
