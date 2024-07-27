package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.lib.domain.ValueObject;

public record UserName(
        String firstName,
        String lastName
) implements ValueObject {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    public UserName {
        this.assertArgumentNotEmpty(firstName, FIRST_NAME, "should not be empty");
        this.assertArgumentNotEmpty(lastName, LAST_NAME, "should not be empty");
        this.assertArgumentMinLength(firstName, 3, FIRST_NAME, "should have at least 3 characters");
        this.assertArgumentMinLength(lastName, 3, LAST_NAME, "should have at least 3 characters");
        this.assertArgumentMaxLength(firstName, 100, FIRST_NAME, "should have at most 100 characters");
        this.assertArgumentMaxLength(lastName, 100, LAST_NAME, "should have at most 100 characters");
    }

    public String fullName() {
        return firstName.concat(" ").concat(lastName);
    }
}
