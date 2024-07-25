package com.kaua.ecommerce.auth.application.usecases.roles.outputs;

import com.kaua.ecommerce.auth.domain.roles.Role;

import java.time.Instant;

public record GetRoleByIdOutput(
        String id,
        String name,
        String description,
        boolean isDefault,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        long version
) {

    public GetRoleByIdOutput(final Role aRole) {
        this(
                aRole.getId().value().toString(),
                aRole.getName().value(),
                aRole.getDescription().value(),
                aRole.isDefault(),
                aRole.isDeleted(),
                aRole.getCreatedAt(),
                aRole.getUpdatedAt(),
                aRole.getDeletedAt().orElse(null),
                aRole.getVersion()
        );
    }
}
