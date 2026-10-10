package com.urlshortenerserver.server.service;

import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.ClickRepository;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.repository.UserRepository;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminStatResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.response.UrlClickStatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listUrls_Success() {
        UrlFilter filter = new UrlFilter();
        Pageable pageable = Pageable.unpaged();
        Url url = new Url(1L, "http://example.com", "TEST", false, null, null);
        Page<Url> urlPage = new PageImpl<>(List.of(url));

        when(urlRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(urlPage);

        PageResponse<?> response = adminService.listUrls(filter, pageable);

        assertNotNull(response);
        verify(urlRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void restoreUrl_Success() {
        String code = "TEST";
        Url url = new Url(1L, "http://example.com", code, true, null, null);

        when(urlRepository.findByCode(code)).thenReturn(Optional.of(url));

        adminService.restoreUrl(code);

        assertFalse(url.isDeleted());
        verify(urlRepository).save(url);
        verify(cacheService).cacheUrl(code, url.getUrl());
    }

    @Test
    void restoreUrl_NotFound_ThrowsException() {
        String code = "UNKNOWN";
        when(urlRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> adminService.restoreUrl(code));
        verify(urlRepository, never()).save(any());
    }

    @Test
    void getUrlClicks_Success() {
        String code = "TEST";
        Pageable pageable = Pageable.unpaged();
        Click click = Click.builder().code(code).ip("127.0.0.1").build();
        Page<Click> clickPage = new PageImpl<>(List.of(click));

        when(urlRepository.existsByCode(code)).thenReturn(true);
        when(clickRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(clickPage);
        when(clickRepository.count(any(Specification.class))).thenReturn(1L);

        UrlClickStatResponse response = adminService.getUrlClicks(code, null, null, pageable);

        assertNotNull(response);
        assertEquals(code, response.getCode());
        assertEquals(1L, response.getTotalClicks());
    }

    @Test
    void getUrlClicks_UrlNotFound_ThrowsException() {
        String code = "UNKNOWN";
        when(urlRepository.existsByCode(code)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> adminService.getUrlClicks(code, null, null, Pageable.unpaged()));
    }

    @Test
    void getSystemStats_Success() {
        when(urlRepository.count()).thenReturn(10L);
        when(clickRepository.count()).thenReturn(50L);
        when(userRepository.count()).thenReturn(5L);
        when(userRepository.countByAdminTrue()).thenReturn(1L);

        AdminStatResponse response = adminService.getSystemStats();

        assertNotNull(response);
        assertEquals(10L, response.getUrls().getTotal());
        assertEquals(50L, response.getClicks().getTotal());
        assertEquals(5L, response.getUsers().getTotal());
        assertEquals(1L, response.getUsers().getAdmins());
    }
}