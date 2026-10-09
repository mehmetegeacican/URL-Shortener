package com.urlshortenerserver.server.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 30, message = "username must be 3-30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "username may only contain letters, digits, '.', '_' and '-'")
    String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 72, message = "password must be 8-72 characters") // BCrypt ignores bytes past 72
    String password;
}
