package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record RoleId(UUID value) implements Identifier<UUID> {

    public RoleId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }

    public RoleId(final String value) {
        this(UUID.fromString(value));
    }
}
