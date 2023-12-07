package com.venticonsulting.authservice.exception.customexceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
