package com.ciarancumiskey.mockitobank.exceptions;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ProblemDetails {
    private @NotNull String message;
    private @NotNull int code;
    private @NotNull String status;
    private @NotNull List<String> details = new ArrayList<>();
    
    public ProblemDetails(final Exception ex, final HttpStatus httpStatus){
        this.message = ex.getLocalizedMessage();
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.details = List.of(String.valueOf(ex.getCause()));
    }
}
