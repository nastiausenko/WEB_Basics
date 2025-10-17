package org.example.lab4.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.lab4.entity.User;
import org.example.lab4.entity.UserResponse;
import org.example.lab4.entity.auth.AuthRequest;
import org.example.lab4.entity.auth.AuthResponse;
import org.example.lab4.security.AccessValidator;
import org.example.lab4.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final AccessValidator accessValidator;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = accessValidator.getCurrentUser();
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail()));
    }
}
