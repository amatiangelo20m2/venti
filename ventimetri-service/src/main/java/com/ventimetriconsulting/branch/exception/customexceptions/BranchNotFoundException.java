package com.ventimetriconsulting.branch.exception.customexceptions;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
