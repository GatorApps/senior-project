package org.gatorapps.garesearch.exception;

import lombok.Getter;

@Getter
public class MalformedParamException extends RuntimeException {
    private final String errCode;

    public MalformedParamException(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

}
