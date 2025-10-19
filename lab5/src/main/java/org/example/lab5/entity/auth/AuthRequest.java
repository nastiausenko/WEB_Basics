package org.example.lab5.entity.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


@Value
@Builder
@Jacksonized
public class AuthRequest {
    String email;
    String username;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+={}\\[\\]|:;\"'<>,.?/~-])[A-Za-z\\d!@#$%^&*()_+={}\\[\\]|:;\"'<>,.?/~-]{8,20}$",
            message = "Password must contain at least one digit, " +
                    "one lowercase letter, one uppercase letter," +
                    " one special character, and be 8–20 characters long")
    String password;
}
