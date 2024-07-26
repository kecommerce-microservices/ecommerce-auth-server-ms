package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record CustomerId(UUID value) implements Identifier<UUID> {

    public CustomerId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }

    public CustomerId(final String value) {
        this(UUID.fromString(value));
    }
}
