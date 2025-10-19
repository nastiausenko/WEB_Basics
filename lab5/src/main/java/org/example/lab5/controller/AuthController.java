package org.example.lab5.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.lab5.entity.auth.AuthRequest;
import org.example.lab5.entity.auth.AuthResponse;
import org.example.lab5.entity.user.User;
import org.example.lab5.entity.user.UserResponse;
import org.example.lab5.security.AccessValidator;
import org.example.lab5.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final AccessValidator accessValidator;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = accessValidator.getCurrentUser();
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail()));
    }
}
