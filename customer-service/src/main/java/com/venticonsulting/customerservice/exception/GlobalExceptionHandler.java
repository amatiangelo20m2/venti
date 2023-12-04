package com.venticonsulting.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public String handleCustomerNotFoundException(CustomerNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(CustomerAlreadyPresentException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public String handleCustomerAlreadyPresentException(CustomerAlreadyPresentException exception) {
        return exception.getMessage();
    }
}
