package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record CreateUserMfaOutput(String userId, String qrCodeUrl) {

    public CreateUserMfaOutput(final User aUser, final String aQrCodeUrl) {
        this(aUser.getId().value().toString(), aQrCodeUrl);
    }
}
