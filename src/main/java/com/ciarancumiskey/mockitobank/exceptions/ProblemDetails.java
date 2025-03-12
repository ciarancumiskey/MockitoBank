package com.ciarancumiskey.mockitobank.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProblemDetails {
    private @NotNull final String message;
    private @NotNull final int code;
    private @NotNull final String status;
    private @NotNull final List<String> details;
    
    public ProblemDetails(final Exception ex, final HttpStatus httpStatus){
        this.message = ex.getLocalizedMessage();
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.details = List.of(String.valueOf(ex.getCause()));
    }
}
