package com.kaua.ecommerce.auth.application.usecases.users.inputs;

import java.util.UUID;

public record CreateUserMfaInput(
        UUID userId,
        String type,
        String deviceName
) {
}
