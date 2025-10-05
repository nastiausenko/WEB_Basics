package org.example.lab.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.lab.dto.auth.AuthResponse;
import org.example.lab.dto.auth.LoginRequest;
import org.example.lab.dto.auth.RegisterRequest;
import org.example.lab.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
