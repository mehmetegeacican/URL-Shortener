package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.exception.InvalidCredentialsException;
import com.urlshortenerserver.server.exception.UsernameAlreadyExistsException;
import com.urlshortenerserver.server.model.User;
import com.urlshortenerserver.server.repository.UserRepository;
import com.urlshortenerserver.server.request.LoginRequest;
import com.urlshortenerserver.server.request.SignUpRequest;
import com.urlshortenerserver.server.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_Success() {
        // Given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("testuser");
        savedUser.setAdmin(false);

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("mock-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        // When
        AuthResponse response = userService.signUp(request);

        // Then
        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void signUp_UsernameAlreadyExists_ThrowsException() {
        // Given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.signUp(request));
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }


    @Test
    void signUp_DataIntegrityViolation_ThrowsException() {
        // Given
        SignUpRequest request = new SignUpRequest();
        request.setUsername("concurrentuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("concurrentuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // Simulate database unique index constraint violation during save
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

        // When & Then
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.signUp(request));
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void login_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");
        user.setAdmin(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mock-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        // When
        AuthResponse response = userService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }
}