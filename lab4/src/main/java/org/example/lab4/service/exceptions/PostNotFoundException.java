package org.example.lab4.service.exceptions;

import org.bson.types.ObjectId;

public class PostNotFoundException extends RuntimeException {
    private static final String MSG = "Post not found with id %s";

    public PostNotFoundException(ObjectId id) {
        super(String.format(MSG, id));
    }
}
