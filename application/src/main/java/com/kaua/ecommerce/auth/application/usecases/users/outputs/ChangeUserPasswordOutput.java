package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record ChangeUserPasswordOutput(String userId) {

    public ChangeUserPasswordOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
