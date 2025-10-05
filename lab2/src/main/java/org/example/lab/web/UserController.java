package org.example.lab.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.lab.dto.auth.AuthResponse;
import org.example.lab.dto.user.ChangePasswordRequest;
import org.example.lab.dto.user.ChangeUsernameRequest;
import org.example.lab.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("new-password")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody @Valid ChangePasswordRequest request) {
        String token = userService.updatePassword(request.getNewPassword());
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    @PutMapping("new-username")
    public ResponseEntity<AuthResponse> updateUsername(@RequestBody @Valid ChangeUsernameRequest request) {
        String token = userService.updateUsername(request.getNewUsername());
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }
}
