package com.urlshortenerserver.server.request.converter;

import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.UrlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlRequestConverterTest {

    private UrlRequestConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UrlRequestConverter();
    }
    @Test
    void convertToEntity() {
        // Given
        UrlRequest request = UrlRequest.builder()
                .url("https://example.com")
                .code("mytest1")
                .build();
        // When
        Url result = converter.convertToEntity(request);

        // Then
        assertEquals("https://example.com", result.getUrl());
        assertEquals("mytest1", result.getCode());
    }

    @Test
    void convertToEntity_withoutCode_leavesCodeNull() {
        UrlRequest request = UrlRequest.builder()
                .url("https://example.com")
                .build();

        Url result = converter.convertToEntity(request);

        assertEquals("https://example.com", result.getUrl());
        assertNull(result.getCode());
    }

    @Test
    void convertToEntity_returnsNewInstanceEachCall() {
        UrlRequest request = UrlRequest.builder()
                .url("https://example.com")
                .code("ABCDE")
                .build();

        Url first = converter.convertToEntity(request);
        Url second = converter.convertToEntity(request);

        assertNotSame(first, second);
    }
}