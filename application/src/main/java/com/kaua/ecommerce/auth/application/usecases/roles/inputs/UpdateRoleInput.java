package com.kaua.ecommerce.auth.application.usecases.roles.inputs;

import java.util.UUID;

public record UpdateRoleInput(
        String roleId,
        String name,
        String description,
        Boolean isDefault
) {

    public UUID getId() {
        return UUID.fromString(roleId);
    }
}
