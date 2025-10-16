package org.example.lab4.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AuthRequest {
    private final String email;
    private final String username;
    private final String password;
}
