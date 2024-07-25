package com.kaua.ecommerce.auth.application.usecases.roles.outputs;

import com.kaua.ecommerce.auth.domain.roles.Role;

public record UpdateRoleOutput(String roleId) {

    public UpdateRoleOutput(final Role aRole) {
        this(aRole.getId().value().toString());
    }
}