package io.github.helpdesk.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserNameRequestDto(
        @NotBlank(message = "UserName is required")
        String userName
) {
}
