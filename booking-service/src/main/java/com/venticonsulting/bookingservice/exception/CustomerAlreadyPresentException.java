package com.venticonsulting.bookingservice.exception;

public class CustomerAlreadyPresentException extends RuntimeException {
    public CustomerAlreadyPresentException(String message) {
        super(message);
    }
}
