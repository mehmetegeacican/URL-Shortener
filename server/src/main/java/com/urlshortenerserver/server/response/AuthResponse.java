package com.urlshortenerserver.server.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponse{
    String token;
    String tokenType;
    long expiresInSeconds;
    UUID userId;
    String username;
    boolean admin;
}
