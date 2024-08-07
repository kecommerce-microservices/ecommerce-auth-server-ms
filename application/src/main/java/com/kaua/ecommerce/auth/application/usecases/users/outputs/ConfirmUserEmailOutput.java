package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record ConfirmUserEmailOutput(String userId) {

    public ConfirmUserEmailOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
