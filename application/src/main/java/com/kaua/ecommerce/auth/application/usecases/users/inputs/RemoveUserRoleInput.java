package com.kaua.ecommerce.auth.application.usecases.users.inputs;

import java.util.UUID;

public record RemoveUserRoleInput(
        UUID id,
        UUID roleId
) {
}
