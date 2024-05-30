package com.guneet.atb.exception;

import io.airbrake.javabrake.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import com.guneet.atb.dtos.response.ApiExceptionResponse;
import com.guneet.atb.dtos.response.ErrorDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    private final Notifier airbrake;

    public CustomExceptionHandler(@Autowired(required = false) Notifier airbrake) {this.airbrake = airbrake;}

    @ExceptionHandler({
            ATBServiceException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleTaskServiceException(ATBServiceException e) {
        log.error("Tasks Service - Application Exception: {} | {}", e.getCode(), e.getStatus());
        if (!CollectionUtils.isEmpty(e.getErrorDetails().getDetails())) {
            log.error("Tasks Service - Error Details: {}", e.getErrorDetails().getDetails());
        }

        ApiExceptionResponse<ErrorDetails> response = new ApiExceptionResponse<>(e.getErrorDetails());
        return new ResponseEntity<>(response, null, e.getCode());
    }

    @ExceptionHandler({
            WebExchangeBindException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleWebExchangeException(WebExchangeBindException e) {
        //For request body validation, Jakarta Validation
        log.error("Tasks Service - Web Exchange Exception: {} | {}", BAD_REQUEST.value(), e.getMessage());
        String[] exceptionArray = e.getMessage().split(";");
        List<Object> details = new ArrayList<>();
        for(int i=5;i<exceptionArray.length; i+=5){
            String msg = org.apache.commons.lang3.StringUtils.substringBetween(exceptionArray[i], "[", "]");
            details.add(msg);
        }
        ErrorDetails errDtls = new ErrorDetails(400, BAD_REQUEST.getReasonPhrase(), e.getReason(), details);
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return new ResponseEntity<>(resp, null, 400);
    }

    @ExceptionHandler({
            ServerWebInputException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleInputException(ServerWebInputException e) {
        //Unknown/ Missing Input in the HTTP Web Request
        log.error("Tasks Service - Server Web Input Exception: {} | {}", BAD_REQUEST.value(), e.getMessage());
        String messageDetails;
        if(e.getCause() instanceof TypeMismatchException origEx) {
            messageDetails = String.format("Parameter '%s' has invalid input value='%s'",
                    origEx.getPropertyName(),
                    origEx.getValue());
        } else {
            messageDetails = e.getCause()==null ? e.getMessage() : e.getCause().getMessage();
        }
        ErrorDetails errDtls = new ErrorDetails(400, e.getReason(), "Invalid query parameter", List.of(messageDetails));
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return new ResponseEntity<>(resp, null, 400);
    }

    @ExceptionHandler({
            NoResourceFoundException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleNoResourceFoundException(NoResourceFoundException e) {
        ErrorDetails errDtls = new ErrorDetails(e.getStatusCode().value(), e.getStatusCode().toString(), e.getReason());
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return new ResponseEntity<>(resp, null, errDtls.getCode());
    }

    @ExceptionHandler({
            MethodNotAllowedException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleMethodNotAllowedException(MethodNotAllowedException e) {
        ErrorDetails errDtls = new ErrorDetails(e.getStatusCode().value(), e.getStatusCode().toString(), e.getReason());
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return new ResponseEntity<>(resp, null, errDtls.getCode());
    }

    @ExceptionHandler({
            UnsupportedMediaTypeStatusException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleMediaTypeException(UnsupportedMediaTypeStatusException e){
        ErrorDetails errDtls = new ErrorDetails(e.getStatusCode().value(), e.getStatusCode().toString(), e.getReason());
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return new ResponseEntity<>(resp, null, errDtls.getCode());
    }

    @ExceptionHandler({
            RuntimeException.class
    })
    public ResponseEntity<ApiExceptionResponse<ErrorDetails>> handleRuntimeException(RuntimeException e) {
        log.error("Tasks Service - Runtime Exception: {} | {}", INTERNAL_SERVER_ERROR.value(), e.getMessage());
        log.error("Runtime Exception Stacktrace: ", e);
        if (airbrake != null) {
            airbrake.report(e);
        }
        ErrorDetails errDtls = new ErrorDetails(INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
        ApiExceptionResponse<ErrorDetails> resp = new ApiExceptionResponse<>(errDtls);
        return ResponseEntity.internalServerError().body(resp);
    }
}
