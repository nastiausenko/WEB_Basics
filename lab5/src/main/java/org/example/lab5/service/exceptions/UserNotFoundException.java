package org.example.lab5.service.exceptions;

import org.bson.types.ObjectId;


public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User with id %s not found";
    private static final String MSG = "User %s not found";

    public UserNotFoundException(ObjectId id) {
        super(String.format(MESSAGE, id));
    }

    public UserNotFoundException(String email) {
        super(String.format(MSG, email));
    }
}
