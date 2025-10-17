package org.example.lab4.security;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab4.entity.User;
import org.example.lab4.repository.UserRepository;
import org.example.lab4.service.exceptions.ForbiddenException;
import org.example.lab4.service.exceptions.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final UserRepository userRepository;

    public void validateOwner(ObjectId ownerId) {
        ObjectId currentUserId = getCurrentUser().getId();
        if (!currentUserId.equals(ownerId)) {
            throw new ForbiddenException();
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
