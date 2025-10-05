package org.example.lab.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class RegisterRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email;

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @NotBlank(message = "Name cannot be null")
    String username;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+={}\\[\\]|:;\"'<>,.?/~-])[A-Za-z\\d!@#$%^&*()_+={}\\[\\]|:;\"'<>,.?/~-]{8,20}$",
            message = "Password must contain at least one digit, " +
                    "one lowercase letter, one uppercase letter," +
                    " one special character, and be 8–20 characters long")
    String password;
}
