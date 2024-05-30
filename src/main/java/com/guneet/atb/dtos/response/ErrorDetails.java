package com.guneet.atb.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private int code;
    private String status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Object> details;

    public ErrorDetails(int code, String status, String message) {
        this(code, status, message, null);
    }
}
