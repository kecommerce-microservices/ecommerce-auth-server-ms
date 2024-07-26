package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record CreateUserOutput(String userId) {

    public CreateUserOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
