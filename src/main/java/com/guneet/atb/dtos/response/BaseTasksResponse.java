package com.guneet.atb.dtos.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseTasksResponse<T> {
    private String type;
    private String timestamp;

    @JsonIgnore
    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetails error;

    protected BaseTasksResponse() {
        this.timestamp = Instant.now().toString();
        this.data = null;
        this.error = null;
    }

    protected BaseTasksResponse(T data) {
        this();
        this.data = data;
    }

    protected BaseTasksResponse(T data, HttpStatus status) {
        this();
        this.data = data;
        this.status = status;
    }

    protected BaseTasksResponse(ErrorDetails error) {
        this();
        this.error = error;
    }

    protected BaseTasksResponse(ErrorDetails error, HttpStatus status) {
        this();
        this.error = error;
        this.status = status;
    }
}
