package com.urlshortenerserver.server.dto.converter;

import com.urlshortenerserver.server.dto.UrlDto;
import com.urlshortenerserver.server.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlDtoConverterTest {

    private UrlDtoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UrlDtoConverter();
    }

    private Url buildUrl(Long id, String url, String code) {
        Url entity = new Url();
        entity.setId(id);
        entity.setUrl(url);
        entity.setCode(code);
        return entity;
    }

    @Test
    void convertToDto() {
        // Given
        Url entity = buildUrl(1L, "https://example.com", "ABCDE");
        // When
        UrlDto dto = converter.convertToDto(entity);
        // Then
        assertEquals(1L, dto.getId());
        assertEquals("https://example.com", dto.getUrl());
        assertEquals("ABCDE", dto.getCode());
    }



    @Test
    void testConvertToDtoList() {
        // Given
        List<Url> entities = List.of(
                buildUrl(1L, "https://one.com", "AAAAA"),
                buildUrl(2L, "https://two.com", "BBBBB"),
                buildUrl(3L, "https://three.com", "CCCCC")
        );
        // When
        List<UrlDto> dtos = converter.convertToDto(entities);
        // Then
        assertEquals(3, dtos.size());
        assertEquals("AAAAA", dtos.get(0).getCode());
        assertEquals("BBBBB", dtos.get(1).getCode());
        assertEquals("CCCCC", dtos.get(2).getCode());
        assertEquals("https://two.com", dtos.get(1).getUrl());
    }
}