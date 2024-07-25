package com.kaua.ecommerce.auth.application.usecases.roles.outputs;

import com.kaua.ecommerce.auth.domain.roles.Role;

import java.time.Instant;

public record GetDefaultRolesOutput(
        String id,
        String name,
        String description,
        boolean isDefault,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt
) {

    public GetDefaultRolesOutput(final Role aRole) {
        this(
                aRole.getId().value().toString(),
                aRole.getName().value(),
                aRole.getDescription().value(),
                aRole.isDefault(),
                aRole.isDeleted(),
                aRole.getCreatedAt(),
                aRole.getUpdatedAt()
        );
    }
}
