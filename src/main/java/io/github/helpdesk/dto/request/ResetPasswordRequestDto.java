package io.github.helpdesk.dto.request;

import io.github.helpdesk.dto.annotation.Password;

public record ResetPasswordRequestDto(
        String token,
        @Password
        String password) {
}
