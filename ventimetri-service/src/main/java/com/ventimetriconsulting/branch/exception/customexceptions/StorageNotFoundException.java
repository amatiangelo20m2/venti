package com.ventimetriconsulting.branch.exception.customexceptions;

public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException(String message) {
        super(message);
    }
}