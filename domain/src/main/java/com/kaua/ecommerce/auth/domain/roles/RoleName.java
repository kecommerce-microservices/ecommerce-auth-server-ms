package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.lib.domain.ValueObject;

public record RoleName(String value) implements ValueObject {

    public RoleName {
        this.assertArgumentNotEmpty(value, "name", "should not be empty");
        this.assertArgumentMinLength(value, 3, "name", "should have at least 3 characters");
        this.assertArgumentMaxLength(value, 100, "name", "should have at most 100 characters");
    }
}
