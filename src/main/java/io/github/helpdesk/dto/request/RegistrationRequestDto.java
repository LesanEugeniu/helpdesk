package io.github.helpdesk.dto.request;

import io.github.helpdesk.dto.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequestDto(
        @NotBlank(message = "UserName is required")
        String userName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @Password
        String password
) {
}
