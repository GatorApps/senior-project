package org.gatorapps.garesearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private String errCode;
    private T payload;

    public ApiResponse(String errCode){
        this.errCode = errCode;
    }
}


