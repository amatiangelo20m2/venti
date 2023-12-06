package com.venticonsulting.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public String handleUserNotFoundException(UserNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleUserNotFoundException(UserAlreadyExistException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleBadCredentialsException(BadCredentialsException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ParseTokenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public String handleBadCredentialsException(ParseTokenException exception) {
        return exception.getMessage();
    }
}
