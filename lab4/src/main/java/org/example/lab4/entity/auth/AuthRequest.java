package org.example.lab4.entity.auth;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


@Value
@Builder
@Jacksonized
public class AuthRequest {
    String email;
    String username;
    String password;
}
