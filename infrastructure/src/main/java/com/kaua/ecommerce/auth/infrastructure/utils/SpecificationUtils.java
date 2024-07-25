package com.kaua.ecommerce.auth.infrastructure.utils;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class SpecificationUtils {

    private SpecificationUtils() {}

    public static <T> Specification<T> like(final String prop, final String term) {
        return (root, query, cb) -> cb.like(cb.upper(root.get(prop)), like(term.toUpperCase()));
    }

    public static <T> Specification<T> between(final String property, final Instant start, final Instant end) {
        return (root, query, cb) -> cb.between(root.get(property), start, end);
    }

    public static <T> Specification<T> isTrue(final String property) {
        return (root, query, cb) -> cb.isTrue(root.get(property));
    }

    private static String like(final String term) {
        return "%" + term + "%";
    }
}
