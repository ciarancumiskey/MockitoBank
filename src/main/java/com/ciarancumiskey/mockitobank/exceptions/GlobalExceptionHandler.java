package com.ciarancumiskey.mockitobank.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ProblemDetails handleException(final Exception ex) {
        return logAndReturnProblemDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProblemDetails handleNotFoundException(final NotFoundException ex) {
        return logAndReturnProblemDetails(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidArgumentsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetails handleBadRequests(final InvalidArgumentsException ex) {
        return logAndReturnProblemDetails(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    ProblemDetails handleEntityConflicts(final AlreadyExistsException ex) {
        return logAndReturnProblemDetails(ex, HttpStatus.CONFLICT);
    }

    private ProblemDetails logAndReturnProblemDetails(final Exception ex, final HttpStatus httpStatus) {
        log.error("Exception occurred: ", ex);
        return new ProblemDetails(ex, httpStatus);
    }
}
