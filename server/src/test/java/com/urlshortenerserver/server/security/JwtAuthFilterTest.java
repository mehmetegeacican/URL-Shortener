package com.urlshortenerserver.server.security;

import com.urlshortenerserver.server.model.User;
import com.urlshortenerserver.server.repository.UserRepository;
import com.urlshortenerserver.server.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidTokenAndUser_SetsAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        Claims claims = mock(Claims.class);
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setAdmin(true);

        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtService.parse(token)).thenReturn(claims);
        Mockito.when(claims.get("uid", String.class)).thenReturn(userId.toString());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_MissingHeader_PassesThroughWithoutAuthentication() throws ServletException, IOException {
        // Given
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_PassesThroughWithoutAuthentication() throws ServletException, IOException {
        // Given
        String token = "invalid-token";
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtService.parse(token)).thenThrow(new JwtException("Invalid token"));

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_UserNotFound_PassesThroughWithoutAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid-token";
        UUID userId = UUID.randomUUID();
        Claims claims = mock(Claims.class);

        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtService.parse(token)).thenReturn(claims);
        Mockito.when(claims.get("uid", String.class)).thenReturn(userId.toString());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}