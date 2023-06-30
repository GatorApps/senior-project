package org.gatorapps.garesearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse<T> {
    private String errCode;
    private Object errMsg;
    private T payload;

    public ErrorResponse(String errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
