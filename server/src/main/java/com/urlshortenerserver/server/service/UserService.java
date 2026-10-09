package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.response.AuthResponse;
import com.urlshortenerserver.server.request.LoginRequest;
import com.urlshortenerserver.server.request.SignUpRequest;
import com.urlshortenerserver.server.exception.InvalidCredentialsException;
import com.urlshortenerserver.server.exception.UsernameAlreadyExistsException;
import com.urlshortenerserver.server.model.User;
import com.urlshortenerserver.server.repository.UserRepository;
import com.urlshortenerserver.server.request.SignUpRequest;
import com.urlshortenerserver.server.response.AuthResponse;
import com.urlshortenerserver.server.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        String username = normalize(request.getUsername());
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAdmin(false); // never taken from the request

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            // Two sign-ups with the same name raced past the exists check; the unique index caught it
            throw new UsernameAlreadyExistsException(username);
        }

        logger.info("User signed up: userId={}", user.getId());
        return toAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(normalize(request.getUsername()))
                .orElseThrow(() -> new InvalidCredentialsException(request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException(request.getUsername());
        }

        logger.info("User logged in: userId={}", user.getId());
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getUsername(),
                user.isAdmin());
    }

    private String normalize(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}