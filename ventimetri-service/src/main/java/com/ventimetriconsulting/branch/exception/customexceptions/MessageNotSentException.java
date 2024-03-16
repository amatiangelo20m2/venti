package com.ventimetriconsulting.branch.exception.customexceptions;

public class MessageNotSentException extends RuntimeException {
    public MessageNotSentException(String message) {
        super(message);
    }
}