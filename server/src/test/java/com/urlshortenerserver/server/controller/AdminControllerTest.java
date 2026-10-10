package com.urlshortenerserver.server.controller;

import com.urlshortenerserver.server.request.filter.UrlFilter;
import com.urlshortenerserver.server.response.AdminStatResponse;
import com.urlshortenerserver.server.response.AdminUrlResponse;
import com.urlshortenerserver.server.response.PageResponse;
import com.urlshortenerserver.server.response.UrlClickStatResponse;
import com.urlshortenerserver.server.service.IAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private IAdminService adminService;

    @InjectMocks
    private AdminController adminController;
    @Test
    void listUrls() {
        // Given
        UrlFilter filter = new UrlFilter();
        PageResponse<AdminUrlResponse> expectedPage = new PageResponse<>();
        Mockito.when(adminService.listUrls(Mockito.eq(filter), Mockito.any(Pageable.class))).thenReturn(expectedPage);

        // When
        ResponseEntity<PageResponse<AdminUrlResponse>> response = adminController.listUrls(0, 20, filter);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
    }

    @Test
    void listUrls_InvalidPage_ThrowsBadRequest() {
        UrlFilter filter = new UrlFilter();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.listUrls(-1, 20, filter);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("page must be 0 or greater"));
    }

    @Test
    void listUrls_InvalidSize_ThrowsBadRequest() {
        UrlFilter filter = new UrlFilter();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.listUrls(0, 150, filter); // Size > MAX_PAGE_SIZE (100)
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("size must be between"));
    }

    @Test
    void restoreUrl() {
        // Given
        String code = "TEST";

        // When
        ResponseEntity<Void> response = adminController.restoreUrl(code);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(adminService).restoreUrl(code);
    }

    @Test
    void getClickStats() {
        // Given
        String code = "TEST";
        UrlClickStatResponse expectedStats = new UrlClickStatResponse();
        Mockito.when(adminService.getUrlClicks(Mockito.eq(code), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class))).thenReturn(expectedStats);

        // When
        ResponseEntity<UrlClickStatResponse> response = adminController.getClickStats(code, 0, 50, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStats, response.getBody());
    }

    @Test
    void getClickStats_InvalidPage_ThrowsBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.getClickStats("TEST", -1, 50, null, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getClickStats_InvalidSize_ThrowsBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.getClickStats("TEST", 0, 0, null, null); // Size < 1
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getSystemStats() {
        // Given
        AdminStatResponse expectedStats = new AdminStatResponse();
        Mockito.when(adminService.getSystemStats()).thenReturn(expectedStats);

        // When
        ResponseEntity<AdminStatResponse> response = adminController.getSystemStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStats, response.getBody());
    }
}