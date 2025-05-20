package io.github.helpdesk.dto.annotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@NotBlank(message = "Password is mandatory")
@Size(min = 8, message = "Password must be at least 8 characters long")
@Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
@Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
@Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
@Pattern(regexp = ".*[@#$%^&+=!].*", message = "Password must contain at least one special character")
public @interface Password
{
}
