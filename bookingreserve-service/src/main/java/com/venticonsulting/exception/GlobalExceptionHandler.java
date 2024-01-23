package com.venticonsulting.exception;

import com.venticonsulting.exception.customException.BranchNotFoundException;
import com.venticonsulting.exception.customException.FormNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BranchNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public String handleUserNotFoundException(BranchNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(FormNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public String handleUserNotFoundException(FormNotFoundException exception) {
        return exception.getMessage();
    }


}
