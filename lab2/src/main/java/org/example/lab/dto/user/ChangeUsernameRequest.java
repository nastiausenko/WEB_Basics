package org.example.lab.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ChangeUsernameRequest {
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @NotBlank(message = "Username is mandatory")
    String newUsername;
}
