package org.gatorapps.garesearch.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class FileDTO {
    private final String fileName;
    private final Instant uploadedTimeStamp;
    private final Map<String, Object> dynamicFields = new HashMap<>();

    public FileDTO(String dynamicIdKey, String id, String fileName, Instant uploadedTimeStamp) {
        this.fileName = fileName;
        this.uploadedTimeStamp = uploadedTimeStamp;
        this.dynamicFields.put(dynamicIdKey, id); // Dynamically set _id field name
    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicFields() {
        return dynamicFields;
    }
}
