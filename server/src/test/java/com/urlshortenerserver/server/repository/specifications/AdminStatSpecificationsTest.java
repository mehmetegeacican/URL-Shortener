package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminStatSpecificationsTest {

    @SuppressWarnings("unchecked")
    private final Root<Url> urlRoot = mock(Root.class);
    @SuppressWarnings("unchecked")
    private final Root<Click> clickRoot = mock(Root.class);
    @SuppressWarnings("unchecked")
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    @BeforeEach
    void setUp() {
        Path<Object> urlPath = mock(Path.class);
        lenient().when(urlRoot.get(anyString())).thenReturn(urlPath);

        Path<Object> clickPath = mock(Path.class);
        lenient().when(clickRoot.get(anyString())).thenReturn(clickPath);

        Predicate predicateMock = mock(Predicate.class);
        lenient().when(criteriaBuilder.equal(any(), any())).thenReturn(predicateMock);
        lenient().when(criteriaBuilder.isNull(any())).thenReturn(predicateMock);
        lenient().when(criteriaBuilder.greaterThanOrEqualTo(any(), any(Instant.class))).thenReturn(predicateMock);
    }

    @Test
    void isDeleted_ReturnsPredicate() {
        // Given
        Specification<Url> spec = AdminStatSpecifications.isDeleted(true);

        // When
        Predicate predicate = spec.toPredicate(urlRoot, query, criteriaBuilder);

        // Then
        assertNull(predicate);
        verify(criteriaBuilder).equal(any(), eq(true));
    }

    @Test
    void isAnonymous_ReturnsPredicate() {
        // Given
        Specification<Url> spec = AdminStatSpecifications.isAnonymous();

        // When
        Predicate predicate = spec.toPredicate(urlRoot, query, criteriaBuilder);

        // Then
        assertNotNull(predicate);
        verify(criteriaBuilder).isNull(any());
    }

    @Test
    void createdAfter_ValidDate_ReturnsPredicate() {
        // Given
        Instant date = Instant.now();
        Specification<Url> spec = AdminStatSpecifications.createdAfter(date);

        // When
        Predicate predicate = spec.toPredicate(urlRoot, query, criteriaBuilder);

        // Then
        assertNotNull(predicate);
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(date));
    }

    @Test
    void createdAfter_NullDate_ReturnsNull() {
        // Given
        Specification<Url> spec = AdminStatSpecifications.createdAfter(null);

        // When
        Predicate predicate = spec.toPredicate(urlRoot, query, criteriaBuilder);

        // Then
        assertNull(predicate);
    }

    @Test
    void clickedAfter_ValidDate_ReturnsPredicate() {
        // Given
        Instant date = Instant.now();
        Specification<Click> spec = AdminStatSpecifications.clickedAfter(date);

        // When
        Predicate predicate = spec.toPredicate(clickRoot, query, criteriaBuilder);

        // Then
        assertNotNull(predicate);
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(date));
    }

    @Test
    void clickedAfter_NullDate_ReturnsNull() {
        // Given
        Specification<Click> spec = AdminStatSpecifications.clickedAfter(null);

        // When
        Predicate predicate = spec.toPredicate(clickRoot, query, criteriaBuilder);

        // Then
        assertNull(predicate);
    }
}