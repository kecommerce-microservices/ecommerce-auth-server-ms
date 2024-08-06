package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record AddRolesToUserOutput(String userId) {

    public AddRolesToUserOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
