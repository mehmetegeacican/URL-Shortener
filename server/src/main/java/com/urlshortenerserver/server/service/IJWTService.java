package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.model.User;
import io.jsonwebtoken.Claims;

public interface IJWTService {
    String generateToken(User user);

    Claims parse(String token);

    long getExpirationSeconds();
}
