package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.request.LoginRequest;
import com.urlshortenerserver.server.request.SignUpRequest;
import com.urlshortenerserver.server.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {
    @Transactional
    AuthResponse signUp(SignUpRequest request);

    @Transactional(readOnly = true)
    AuthResponse login(LoginRequest request);
}
