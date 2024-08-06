package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;

public record RemoveUserRoleOutput(String userId) {

    public RemoveUserRoleOutput(final User aUser) {
        this(aUser.getId().value().toString());
    }
}
