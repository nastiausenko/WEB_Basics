package org.example.lab5.security;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.example.lab5.entity.user.User;
import org.example.lab5.repository.UserRepository;
import org.example.lab5.security.noauth.MockUserProperties;
import org.example.lab5.service.exceptions.ForbiddenException;
import org.example.lab5.service.exceptions.UserNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final UserRepository userRepository;
    private final MockUserProperties mockUserProperties;
    private final Environment environment;

    public void validateOwner(ObjectId ownerId) {
        ObjectId currentUserId = getCurrentUser().getId();
        if (!currentUserId.equals(ownerId)) {
            throw new ForbiddenException();
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNoAuthProfile()) {
            return mockUserProperties.toUserEntity();
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    private boolean isNoAuthProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("no-auth");
    }
}
