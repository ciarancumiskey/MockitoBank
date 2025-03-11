package com.ciarancumiskey.mockitobank.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidArgumentsException extends Exception {
    public InvalidArgumentsException(final String message){
        super(message);
    }
}
