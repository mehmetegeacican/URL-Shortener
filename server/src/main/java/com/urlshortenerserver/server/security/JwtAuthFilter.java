package com.urlshortenerserver.server.security;

import com.urlshortenerserver.server.repository.UserRepository;
import com.urlshortenerserver.server.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Reads "Authorization: Bearer <token>", loads the User from the database, and authenticates the request
 * with the User entity as the principal. A missing or invalid token, or a user that no longer exists,
 * leaves the request anonymous, and the security rules decide what happens next.
 * Deliberately not a @Component: it is created in SecurityConfig so it only runs in the security chain.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7));
                UUID userId = UUID.fromString(claims.get("uid", String.class));
                // The database is the source of truth for roles, so demotions and deletions apply immediately
                userRepository.findById(userId).ifPresentOrElse(user -> {
                    var authorities = List.of(new SimpleGrantedAuthority(user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null, authorities));
                }, () -> log.debug("JWT refers to a user that no longer exists: userId={}", userId));
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("Rejected invalid JWT: {}", e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}