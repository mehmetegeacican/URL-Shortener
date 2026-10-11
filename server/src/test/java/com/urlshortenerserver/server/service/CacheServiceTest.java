package com.urlshortenerserver.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getUrlFromCache_Success() {
        // Given
        String code = "test";
        String expectedUrl = "http://example.com";
        Mockito.when(valueOperations.get("redirect:" + code)).thenReturn(expectedUrl);

        // When
        String actualUrl = cacheService.getUrlFromCache(code);

        // Then
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void getUrlFromCache_Exception_ReturnsNull() {
        // Given
        String code = "test";
        Mockito.when(valueOperations.get(any())).thenThrow(new RuntimeException("Redis down"));

        // When
        String actualUrl = cacheService.getUrlFromCache(code);

        // Then
        assertNull(actualUrl);
    }

    @Test
    void cacheUrl_Success() {
        // Given
        String code = "test";
        String url = "http://example.com";

        // When
        cacheService.cacheUrl(code, url);

        // Then
        verify(valueOperations).set(eq("redirect:" + code), eq(url), any(Duration.class));
    }

    @Test
    void cacheUrl_Exception_HandledGracefully() {
        // Given
        String code = "test";
        String url = "http://example.com";
        doThrow(new RuntimeException("Redis down")).when(valueOperations).set(any(), any(), any());

        // When & Then
        assertDoesNotThrow(() -> cacheService.cacheUrl(code, url));
    }

    @Test
    void invalidateCache_Success() {
        // Given
        String code = "test";

        // When
        cacheService.invalidateCache(code);

        // Then
        verify(redisTemplate).delete("redirect:" + code);
    }

    @Test
    void invalidateCache_Exception_HandledGracefully() {
        // Given
        String code = "test";
        doThrow(new RuntimeException("Redis down")).when(redisTemplate).delete(anyString());

        // When & Then
        assertDoesNotThrow(() -> cacheService.invalidateCache(code));
    }
}