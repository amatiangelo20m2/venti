package com.venticonsulting.exception.customException;

public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(String message) {
        super(message);
    }
}
