package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    @Test
    void hasUserIdOrNull_WhenUserIdIsNotNull_ReturnsOrPredicate() {
        // Given
        UUID userId = UUID.randomUUID();
        Specification<Url> spec = UrlSpecifications.hasUserIdOrNull(userId);

        Path<Object> userPath = Mockito.mock(Path.class);
        Predicate equalsPredicate = Mockito.mock(Predicate.class);
        Predicate isNullPredicate = Mockito.mock(Predicate.class);
        Predicate orPredicate = Mockito.mock(Predicate.class);

        Mockito.when(root.get("userId")).thenReturn(userPath);
        Mockito.when(criteriaBuilder.equal(userPath, userId)).thenReturn(equalsPredicate);
        Mockito.when(criteriaBuilder.isNull(userPath)).thenReturn(isNullPredicate);
        Mockito.when(criteriaBuilder.or(equalsPredicate, isNullPredicate)).thenReturn(orPredicate);

        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNotNull(predicate);
        assertEquals(orPredicate, predicate);
    }

    @Test
    void hasUserIdOrNull_WhenUserIdIsNull_ReturnsIsNullPredicate() {
        // Given
        Specification<Url> spec = UrlSpecifications.hasUserIdOrNull(null);
        Path<Object> userPath = Mockito.mock(Path.class);
        Predicate isNullPredicate = Mockito.mock(Predicate.class);

        Mockito.when(root.get("userId")).thenReturn(userPath);
        Mockito.when(criteriaBuilder.isNull(userPath)).thenReturn(isNullPredicate);

        // When
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertNotNull(predicate);
        assertEquals(isNullPredicate, predicate);
        Mockito.verify(criteriaBuilder).isNull(userPath);
    }
}