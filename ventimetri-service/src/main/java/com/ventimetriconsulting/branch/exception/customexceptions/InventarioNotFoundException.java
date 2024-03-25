package com.ventimetriconsulting.branch.exception.customexceptions;

public class InventarioNotFoundException extends RuntimeException{
    public InventarioNotFoundException(String message) {
        super(message);
    }
}
