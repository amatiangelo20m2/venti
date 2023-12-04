package com.venticonsulting.customerservice.exception;

public class CustomerAlreadyPresentException extends RuntimeException {
    public CustomerAlreadyPresentException(String message) {
        super(message);
    }
}
