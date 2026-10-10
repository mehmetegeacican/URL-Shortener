package com.urlshortenerserver.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ICacheServiceDefaultMethodsTest {

    private ICacheService cacheService;

    @BeforeEach
    void setUp() {
        // Anonymous implementation to test the interface's default method
        cacheService = new ICacheService() {
            @Override
            public String getUrlFromCache(String code) {
                return null;
            }

            @Override
            public void cacheUrl(String code, String url) {
            }

            @Override
            public void invalidateCache(String code) {
            }
        };
    }

    @Test
    void buildCacheKey_ValidCode_ReturnsFormattedUppercaseKey() {
        // Given
        String code = "test123";

        // When
        String cacheKey = cacheService.buildCacheKey(code);

        // Then
        assertEquals("redirect:TEST123", cacheKey);
    }

    @Test
    void buildCacheKey_AlreadyUppercaseCode_ReturnsFormattedKey() {
        // Given
        String code = "MYCODE";

        // When
        String cacheKey = cacheService.buildCacheKey(code);

        // Then
        assertEquals("redirect:MYCODE", cacheKey);
    }
}