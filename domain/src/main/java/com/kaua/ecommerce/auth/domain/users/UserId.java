package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record UserId(UUID value) implements Identifier<UUID> {

    public UserId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }

    public UserId(final String value) {
        this(UUID.fromString(value));
    }
}
