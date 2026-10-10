package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Secret must be at least 32 bytes
        String secret = "mySecretKey123456789012345678901234";
        long expirationMinutes = 60;
        jwtService = new JWTService(secret, expirationMinutes);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setAdmin(true);
    }

    @Test
    void generateTokenAndParse_Success() {
        // Given
        // When
        String token = jwtService.generateToken(testUser);
        Claims claims = jwtService.parse(token);

        // Then
        assertNotNull(token);
        assertEquals("testuser", claims.getSubject());
        assertEquals(testUser.getId().toString(), claims.get("uid", String.class));
        assertEquals(true, claims.get("admin", Boolean.class));
    }

    @Test
    void getExpirationSeconds_Success() {
        // Given
        // When
        long expirationSeconds = jwtService.getExpirationSeconds();

        // Then
        assertEquals(3600L, expirationSeconds); // 60 minutes * 60 seconds
    }
}