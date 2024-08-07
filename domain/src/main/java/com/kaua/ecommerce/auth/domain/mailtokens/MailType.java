package com.kaua.ecommerce.auth.domain.mailtokens;

import java.util.Arrays;
import java.util.Optional;

public enum MailType {

    EMAIL_CONFIRMATION,
    PASSWORD_RESET;

    public static Optional<MailType> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
