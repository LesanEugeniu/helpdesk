package io.github.helpdesk.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordEmailRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email) {
}
