package com.kaua.ecommerce.auth.domain.mailtokens;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record MailId(UUID value) implements Identifier<UUID> {

    public MailId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }

    public MailId(String value) {
        this(UUID.fromString(value));
    }
}
