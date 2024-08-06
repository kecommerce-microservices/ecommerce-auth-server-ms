package com.kaua.ecommerce.auth.application.usecases.users.inputs;

import java.util.Set;
import java.util.UUID;

public record AddRolesToUserInput(
        UUID id,
        Set<UUID> rolesIds
) {
}
