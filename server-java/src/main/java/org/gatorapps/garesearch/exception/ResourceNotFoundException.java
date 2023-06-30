package org.gatorapps.garesearch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String errCode;

    public ResourceNotFoundException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

}
