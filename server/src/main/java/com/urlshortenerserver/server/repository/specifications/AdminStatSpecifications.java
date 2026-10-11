package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Click;
import com.urlshortenerserver.server.model.Url;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class AdminStatSpecifications {
    public static Specification<Url> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<Url> isAnonymous() {
        return (root, query, cb) -> cb.isNull(root.get("userId"));
    }

    public static Specification<Url> createdAfter(Instant date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    public static Specification<Click> clickedAfter(Instant date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.greaterThanOrEqualTo(root.get("clickedAt"), date);
        };
    }
}
