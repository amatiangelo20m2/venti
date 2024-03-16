package com.ventimetriconsulting.branch.exception.customexceptions;

public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(String message) {
        super(message);
    }
}