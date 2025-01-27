package org.gatorapps.garesearch.exception;

import lombok.Getter;

@Getter
public class UnwantedResult extends RuntimeException {
    private final String errCode;

    public UnwantedResult(String errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

}