package org.example.lab4.security;

import org.bson.types.ObjectId;
import org.example.lab4.service.exceptions.ForbiddenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccessValidator {
    public void validateOwner(ObjectId currentUserId, ObjectId ownerId) {
        if (!currentUserId.equals(ownerId)) {
            throw new ForbiddenException();
        }
    }
}
