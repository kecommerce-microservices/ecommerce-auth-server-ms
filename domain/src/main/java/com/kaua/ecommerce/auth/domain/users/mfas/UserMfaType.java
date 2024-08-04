package com.kaua.ecommerce.auth.domain.users.mfas;

import java.util.Arrays;
import java.util.Optional;

public enum UserMfaType {

    TOTP,
    EMAIL;

    public static Optional<UserMfaType> of(final String type) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(type))
                .findFirst();
    }
}
