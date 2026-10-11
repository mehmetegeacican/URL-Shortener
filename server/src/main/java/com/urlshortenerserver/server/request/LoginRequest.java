package com.urlshortenerserver.server.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {
    @NotBlank(message = "username is required") String username;
    @NotBlank(message = "password is required") String password;
}
