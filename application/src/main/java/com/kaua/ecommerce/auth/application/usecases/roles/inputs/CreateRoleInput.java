package com.kaua.ecommerce.auth.application.usecases.roles.inputs;

public record CreateRoleInput(
        String name,
        String description,
        boolean isDefault
) {
}
