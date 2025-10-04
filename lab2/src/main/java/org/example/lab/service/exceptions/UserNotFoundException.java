package org.example.lab.service.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User with id %s not found";
    private static final String MSG = "User not found with email %s";

    public UserNotFoundException(UUID id) {
        super(String.format(MESSAGE, id));
    }

    public UserNotFoundException(String email) {
        super(String.format(MSG, email));
    }
}
