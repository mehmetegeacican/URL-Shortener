package com.urlshortenerserver.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class CacheServiceTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService(redisTemplate);

    }

    @Test
    void getUrlFromCache_Hit() {
        // Given
        String code = "TEST";
        String cachedUrl = "https://example.com";
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get("redirect:TEST")).thenReturn(cachedUrl);

        // When
        String result = cacheService.getUrlFromCache(code);

        // Then
        assertEquals(cachedUrl, result);
        Mockito.verify(valueOperations, Mockito.times(1)).get("redirect:TEST");
    }

    @Test
    void testGetUrlFromCache_Miss() {
        // Given
        String code = "TEST";
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get("redirect:TEST")).thenReturn(null);

        // When
        String result = cacheService.getUrlFromCache(code);

        // Then
        assertNull(result);
        Mockito.verify(valueOperations, Mockito.times(1)).get("redirect:TEST");
    }

    @Test
    void cacheUrl() {
        // Given
        String code = "TEST";
        String url = "https://example.com";
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // When
        cacheService.cacheUrl(code, url);

        // Then
        Mockito.verify(valueOperations, Mockito.times(1)).set(
                Mockito.eq("redirect:TEST"),
                Mockito.eq("https://example.com"),
                any()
        );
    }

    @Test
    void invalidateCache() {
        // Given
        String code = "TEST";

        // When
        cacheService.invalidateCache(code);

        // Then
        Mockito.verify(redisTemplate, Mockito.times(1)).delete("redirect:TEST");
    }
}