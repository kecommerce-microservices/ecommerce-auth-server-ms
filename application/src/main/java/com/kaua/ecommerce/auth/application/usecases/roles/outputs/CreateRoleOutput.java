package com.kaua.ecommerce.auth.application.usecases.roles.outputs;

import com.kaua.ecommerce.auth.domain.roles.Role;

public record CreateRoleOutput(String roleId) {

    public CreateRoleOutput(final Role aRole) {
        this(aRole.getId().value().toString());
    }
}
