package org.example.lab.service;

import lombok.RequiredArgsConstructor;
import org.example.lab.entity.User;
import org.example.lab.repository.UserRepository;
import org.example.lab.security.jwt.JwtUtil;
import org.example.lab.service.exceptions.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String updatePassword(String newPassword) {
        User user = getCurrentUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return jwtUtil.generateToken(user.getEmail(), user.getRoles());
    }

    public String updateUsername(String newUsername) {
        User user = getCurrentUser();

        user.setUsername(newUsername);
        userRepository.save(user);

        return jwtUtil.generateToken(user.getEmail(), user.getRoles());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
