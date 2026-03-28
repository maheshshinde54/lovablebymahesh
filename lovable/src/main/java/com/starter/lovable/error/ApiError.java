package com.starter.lovable.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record ApiError(
        HttpStatus status,
        String message,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ApiFiledError>errors) {

    public ApiError(HttpStatus status, String message)
    {
        this(status,message,Instant.now(),null);
    }

    public ApiError(HttpStatus status, String message,List<ApiFiledError> errors)
    {
        this(status,message,Instant.now(),errors);
    }

}

