package org.example.lab4.entity.user;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record UserResponse(String username, String email) { }
