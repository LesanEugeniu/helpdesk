package io.github.helpdesk.dto.request;

import io.github.helpdesk.dto.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(
        @NotBlank(message = "UserName is required")
        String userName,

        @Password
        String password
) {
}
