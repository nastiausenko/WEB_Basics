package org.example.lab4.service;

import lombok.RequiredArgsConstructor;
import org.example.lab4.entity.AuthRequest;
import org.example.lab4.entity.AuthResponse;
import org.example.lab4.entity.User;
import org.example.lab4.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public String register(AuthRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username " + request.getUsername() + " already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userRepository.save(user);
        return "OK";
    }

    public User login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User with email " + request.getEmail() + " does not exist");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Bad Credentials");
        }

        return user;
    }
}
