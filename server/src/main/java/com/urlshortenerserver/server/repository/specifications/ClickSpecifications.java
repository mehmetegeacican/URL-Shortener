package com.urlshortenerserver.server.repository.specifications;

import com.urlshortenerserver.server.model.Click;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class ClickSpecifications {

    public static Specification<Click> withFilters(String code, Instant from, Instant to) {
        return Specification.where(hasCode(code))
                .and(clickedBetween(from, to));
    }
    private static Specification<Click> hasCode(String code) {
        return (root, query, cb) -> cb.equal(root.get("code"), code);
    }
    private static Specification<Click> clickedBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("clickedAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("clickedAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("clickedAt"), to);
        };
    }
}
