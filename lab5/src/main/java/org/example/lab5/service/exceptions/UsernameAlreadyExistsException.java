package org.example.lab5.service.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Username %s is already in use";

    public UsernameAlreadyExistsException(String username) {
        super(String.format(MESSAGE, username));
    }
}
