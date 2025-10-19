package org.example.lab5.service.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Email %s is already in use";
    public EmailAlreadyExistsException(String email) {
        super(String.format(MESSAGE, email));
    }
}
