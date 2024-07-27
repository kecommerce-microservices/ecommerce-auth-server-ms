package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.lib.domain.ValueObject;

import java.util.regex.Pattern;

public record UserPassword(String value) implements ValueObject {

    private static final String PASSWORD = "password";

    private static final String PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$")
            .pattern();

    public UserPassword {
        this.assertArgumentNotEmpty(value, PASSWORD, "should not be empty");
    }

    public static UserPassword create(final String rawPassword) {
        final var aPassword = new UserPassword(rawPassword);
        aPassword.assertArgumentNotEmpty(rawPassword, PASSWORD, "should not be empty");
        aPassword.assertArgumentMinLength(rawPassword, 8, PASSWORD, "should have at least 8 characters");
        aPassword.assertArgumentMaxLength(rawPassword, 255, PASSWORD, "should have at most 255 characters");
        aPassword.assertArgumentPattern(rawPassword, PASSWORD_PATTERN, PASSWORD, "should have at least one lowercase letter, one uppercase letter, one digit and one special character");
        return aPassword;
    }
}
