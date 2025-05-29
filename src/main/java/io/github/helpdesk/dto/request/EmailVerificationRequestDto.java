package io.github.helpdesk.dto.request;

import io.github.helpdesk.dto.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequestDto(
        @NotBlank(message = "UserName is required")
        String userName,

        @NotBlank(message = "Otp is required")
        String otp,

        @Password
        String password
) {
}
