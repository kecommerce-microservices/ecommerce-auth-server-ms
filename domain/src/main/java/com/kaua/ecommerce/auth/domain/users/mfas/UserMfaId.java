package com.kaua.ecommerce.auth.domain.users.mfas;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record UserMfaId(UUID value) implements Identifier<UUID> {

    public UserMfaId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }

    public UserMfaId(final String value) {
        this(UUID.fromString(value));
    }
}
