package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Url;
import com.urlshortenerserver.server.request.filter.UrlFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UrlSpecifications {

    public static Specification<Url> withFilters(UrlFilter filter) {
        if (filter == null) {
            return Specification.where(null);
        }
        return Specification.where(hasUrl(filter.getUrl()))
                .and(hasCode(filter.getCode()))
                .and(hasUserId(filter.getUserId()));
    }

    public static Specification<Url> hasUrl(String url) {
        return (root, query, criteriaBuilder) -> {
            if (url == null || url.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("url")), "%" + url.toLowerCase() + "%");
        };
    }

    public static Specification<Url> hasCode(String code) {
        return (root, query, criteriaBuilder) -> {
            if (code == null || code.trim().isEmpty()) {
                return null;
            }
            // Using exact match or partial match depending on preference (here using partial/like)
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + code.toLowerCase() + "%");
        };
    }

    public static Specification<Url> hasUserId(UUID userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return null;
            }
            // Exact match for UUID
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }
}
