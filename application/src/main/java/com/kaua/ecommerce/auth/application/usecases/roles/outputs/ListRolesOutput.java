package com.kaua.ecommerce.auth.application.usecases.roles.outputs;

import com.kaua.ecommerce.auth.domain.roles.Role;

import java.time.Instant;

public record ListRolesOutput(
        String id,
        String name,
        String description,
        boolean isDefault,
        boolean isDeleted,
        Instant createdAt,
        Instant deletedAt
) {

    public ListRolesOutput(final Role aRole) {
        this(
                aRole.getId().value().toString(),
                aRole.getName().value(),
                aRole.getDescription().value(),
                aRole.isDefault(),
                aRole.isDeleted(),
                aRole.getCreatedAt(),
                aRole.getDeletedAt().orElse(null)
        );
    }
}