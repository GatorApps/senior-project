package org.gatorapps.garesearch.model.garesearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gatorapps.garesearch.validators.OpidExists;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Document(collection = "files")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {
    @Id
    private String id;

    @Field("opid")
    // @NotBlank(message = "File uploader opid is required")
    @Indexed(unique=true)
    @OpidExists
    private String opid;

    @CreatedDate
    @Field("uploadedTimeStamp")
    // @NotNull(message = "uploadedTimeStamp is required")
    private Date uploadedTimeStamp;

    @Field("category")
    @Pattern(regexp = "resume|transcript", message = "File category must be one of 'resume', 'transcript'")
    private String category;

    @Field("name")
    private String name;

    @Field("s3Path")
    private String s3Path;

    public File(String opid, String category, String name, String s3Path) {
        this.opid = opid;
        this.category = category;
        this.name = name;
        this.s3Path = s3Path;
    }
}
