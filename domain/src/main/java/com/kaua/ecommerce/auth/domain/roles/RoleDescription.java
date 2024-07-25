package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.lib.domain.ValueObject;

public record RoleDescription(String value) implements ValueObject {

    public RoleDescription {
        this.assertArgumentNotNull(value, "description", "should not be null");
        this.assertArgumentMaxLength(value, 255, "description", "should have at most 255 characters");
    }
}
