package org.gatorapps.garesearch.model.garesearch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
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

    @Field("fileName")
    private String fileName;

    @Field("fileS3Path")
    private String fileS3Path;

    public File(String opid, String category, String fileName, String fileS3Path) {
        this.opid = opid;
        this.category = category;
        this.fileName = fileName;
        this.fileS3Path = fileS3Path;
    }
}
