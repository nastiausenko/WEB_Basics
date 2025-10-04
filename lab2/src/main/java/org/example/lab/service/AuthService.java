package org.example.lab.service;

import lombok.RequiredArgsConstructor;
import org.example.lab.dto.AuthResponse;
import org.example.lab.dto.LoginRequest;
import org.example.lab.dto.RegisterRequest;
import org.example.lab.entity.User;
import org.example.lab.repository.UserRepository;
import org.example.lab.security.jwt.JwtUtil;
import org.example.lab.service.exceptions.EmailAlreadyExistsException;
import org.example.lab.service.exceptions.UserNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException(request.getEmail()));
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.builder().token(token).build();
    }
}
