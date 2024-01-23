package com.venticonsulting.exception.customException;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
