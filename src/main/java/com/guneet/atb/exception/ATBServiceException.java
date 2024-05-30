package com.guneet.atb.exception;



import com.guneet.atb.constants.ErrorCodes;
import com.guneet.atb.dtos.response.ErrorDetails;

import lombok.Getter;

@Getter
public class ATBServiceException extends RuntimeException {
    private final int code;
    private final String status;
    private final ErrorDetails errorDetails;

    public ATBServiceException(int code, String status, String message) {
        this(code, status, message, null);
    }

    public ATBServiceException(int code, String status, String message, Throwable ex) {
        super(message, ex);

        this.code = code;
        this.status = status;
        this.errorDetails = new ErrorDetails(code, status, message);
    }

    public ATBServiceException(ErrorCodes err) {
        this(err.getCode().value(), err.getStatus(), err.getMessage());
    }
}
