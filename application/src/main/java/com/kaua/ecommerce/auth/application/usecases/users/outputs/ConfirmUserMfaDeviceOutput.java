package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record ConfirmUserMfaDeviceOutput(String userId) {

    public ConfirmUserMfaDeviceOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
