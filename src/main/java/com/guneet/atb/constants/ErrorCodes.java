package com.guneet.atb.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodes {
    _NOT_FOUND(HttpStatus.NOT_FOUND, "Not a valid Id"),
    _TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Not a valid  Type"),
    _CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create "),
    SITE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Not a valid Site");

    private final HttpStatus code;
    private final String message;

    ErrorCodes(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatus() {
        return name();
    }
}
