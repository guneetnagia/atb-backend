package com.guneet.atb.dtos.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiExceptionResponse<T> extends BaseTasksResponse<T>{
    public ApiExceptionResponse(ErrorDetails error) {
        super(error);
    }

    @Override
    public String getType() {
        return "atb-service-error";
    }
}
