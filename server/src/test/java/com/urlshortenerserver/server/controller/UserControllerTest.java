package com.urlshortenerserver.server.controller;

import com.urlshortenerserver.server.request.LoginRequest;
import com.urlshortenerserver.server.request.SignUpRequest;
import com.urlshortenerserver.server.response.AuthResponse;
import com.urlshortenerserver.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Initializes the mocks and injects userService into userController
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp() {
        // Given
        SignUpRequest request = new SignUpRequest();
        AuthResponse expectedResponse = new AuthResponse();

        // When
        Mockito.when(userService.signUp(request)).thenReturn(expectedResponse);

        // Then
        ResponseEntity<AuthResponse> response = userController.signUp(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void login() {
        // Given
        LoginRequest request = new LoginRequest();
        AuthResponse expectedResponse = new AuthResponse();

        // When
        Mockito.when(userService.login(request)).thenReturn(expectedResponse);

        // Then
        ResponseEntity<AuthResponse> response = userController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}