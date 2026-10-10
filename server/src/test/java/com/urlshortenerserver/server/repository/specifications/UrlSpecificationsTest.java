package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class UrlSpecificationsTest {

    @SuppressWarnings("unchecked")
    private final Root<Url> root = mock(Root.class);
    @SuppressWarnings("unchecked")
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    @Test
    void withFilters_NullFilter_ReturnsValidSpecification() {
        // Given
        UrlFilter filter = null;

        // When
        Specification<Url> spec = UrlSpecifications.withFilters(filter);

        // Then
        assertNotNull(spec);
        // Evaluating the spec with null/empty filter should return null predicate
        assertNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void hasUrl_ValidUrl_ReturnsPredicate() {
        // Given
        String urlQuery = "example";
        Specification<Url> spec = UrlSpecifications.hasUrl(urlQuery);

        // When
        spec.toPredicate(root, query, criteriaBuilder);

        // Then
        verify(criteriaBuilder).like(any(), eq("%example%"));
    }

    @Test
    void hasUrl_BlankUrl_ReturnsNull() {
        // Given
        Specification<Url> spec = UrlSpecifications.hasUrl("   ");

        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(predicate);
    }

    @Test
    void hasCode_ValidCode_ReturnsPredicate() {
        // Given
        String codeQuery = "test";
        Specification<Url> spec = UrlSpecifications.hasCode(codeQuery);

        // When
        spec.toPredicate(root, query, criteriaBuilder);

        // Then
        verify(criteriaBuilder).like(any(), eq("%test%"));
    }

    @Test
    void hasCode_NullCode_ReturnsNull() {
        // Given
        Specification<Url> spec = UrlSpecifications.hasCode(null);

        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(predicate);
    }

    @Test
    void hasUserId_ValidId_ReturnsPredicate() {
        // Given
        UUID userId = UUID.randomUUID();
        Specification<Url> spec = UrlSpecifications.hasUserId(userId);

        // When
        spec.toPredicate(root, query, criteriaBuilder);

        // Then
        verify(criteriaBuilder).equal(any(), eq(userId));
    }

    @Test
    void hasUserId_NullId_ReturnsNull() {
        // Given
        Specification<Url> spec = UrlSpecifications.hasUserId(null);

        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNull(predicate);
    }
}