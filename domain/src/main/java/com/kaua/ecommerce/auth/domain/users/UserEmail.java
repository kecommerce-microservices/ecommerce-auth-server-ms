package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.lib.domain.ValueObject;

import java.util.regex.Pattern;

public record UserEmail(String value) implements ValueObject {

    private static final String EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+){0,5}\\.[a-zA-Z]{2,5}$")
            .pattern();

    public UserEmail {
        this.assertArgumentNotEmpty(value, "email", "should not be empty");
        this.assertArgumentPattern(value, EMAIL_PATTERN, "email", "should be a valid email address");
    }
}