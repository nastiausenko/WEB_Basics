package org.example.lab.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ChangeUsernameRequest {
    @NotBlank(message = "Username is mandatory")
    String newUsername;
}
