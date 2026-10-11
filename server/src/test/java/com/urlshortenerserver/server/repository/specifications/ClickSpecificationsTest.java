package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.repository.ClickRepository;
import com.urlshortenerserver.server.repository.UrlRepository;
import com.urlshortenerserver.server.response.AdminStatResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClickSpecificationsTest {

    @SuppressWarnings("unchecked")
    private final Root<Click> root = mock(Root.class);
    @SuppressWarnings("unchecked")
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    @BeforeEach
    void setUp() {
        // Prevent root.get(...) from returning null to avoid NullPointerException in CriteriaBuilder
        Path<Object> path = mock(Path.class);
        lenient().when(root.get(anyString())).thenReturn(path);
    }

    @Test
    void withFilters_ValidParameters_ReturnsSpecification() {
        // Given
        String code = "test";
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();

        // Stub criteriaBuilder methods to return a non-null Predicate mock
        Predicate predicateMock = mock(Predicate.class);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicateMock);
        when(criteriaBuilder.between(any(), any(Instant.class), any(Instant.class))).thenReturn(predicateMock);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicateMock);

        // When
        Specification<Click> spec = ClickSpecifications.withFilters(code, from, to);

        // Then
        assertNotNull(spec);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void clickedBetween_BothNull_ReturnsNull() {
        // Given
        Specification<Click> spec = ClickSpecifications.withFilters("test", null, null);



        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(predicate); // hasCode predicate is present
    }

    @Test
    void getTopIps_ReturnsTopIpsList() {
        // Given
        ClickRepository clickRepository = mock(ClickRepository.class);
        Click click = Click.builder().ip("127.0.0.1").code("test").build();
        when(clickRepository.findAll()).thenReturn(List.of(click));

        // When
        List<AdminStatResponse.TopIpDto> topIps = ClickSpecifications.getTopIps(clickRepository, 5);

        // Then
        assertNotNull(topIps);
        assertEquals(1, topIps.size());
        assertEquals("127.0.0.1", topIps.get(0).getIp());
    }

    @Test
    void getTopUrls_ReturnsTopUrlsList() {
        // Given
        ClickRepository clickRepository = mock(ClickRepository.class);
        UrlRepository urlRepository = mock(UrlRepository.class);
        Click click = Click.builder().code("test").build();
        Url url = new Url(1L, "http://example.com", "test", false, null, null);

        when(clickRepository.findAll()).thenReturn(List.of(click));
        when(urlRepository.findAll()).thenReturn(List.of(url));

        // When
        List<AdminStatResponse.TopUrlDto> topUrls = ClickSpecifications.getTopUrls(clickRepository, urlRepository, 5);

        // Then
        assertNotNull(topUrls);
        assertEquals(1, topUrls.size());
        assertEquals("test", topUrls.get(0).getCode());
        assertEquals("http://example.com", topUrls.get(0).getUrl());
    }

    @Test
    void getClicksPerDay_ReturnsClicksPerDayList() {
        // Given
        ClickRepository clickRepository = mock(ClickRepository.class);
        Click click = Click.builder().clickedAt(Instant.now()).code("test").build();
        when(clickRepository.findAll()).thenReturn(List.of(click));

        // When
        List<AdminStatResponse.ClicksPerDayDto> clicksPerDay = ClickSpecifications.getClicksPerDay(clickRepository, 7);

        // Then
        assertNotNull(clicksPerDay);
        assertEquals(1, clicksPerDay.size());
    }
}