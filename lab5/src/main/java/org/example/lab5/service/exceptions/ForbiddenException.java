package org.example.lab5.service.exceptions;

public class ForbiddenException extends RuntimeException {
    private static final String MESSAGE = "Access denied";
    public ForbiddenException() {
        super(MESSAGE);
    }
}
