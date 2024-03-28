package com.venticonsulting.authservice.exception.customexceptions;

public class ParseTokenException extends RuntimeException {
    public ParseTokenException(String message) {
        super(message);
    }
}