package org.example.lab.dto.auth;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuthResponse {
    String token;
}
