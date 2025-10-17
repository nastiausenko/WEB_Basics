package org.example.lab4.service.exceptions;

public class ForbiddenException extends RuntimeException {
    private static final String MESSAGE = "Access denied";
    public ForbiddenException() {
        super(MESSAGE);
    }
}
