package com.kaua.ecommerce.auth.application.usecases.users.inputs;

import java.time.Instant;
import java.util.UUID;

public record ConfirmUserMfaDeviceInput(
        UUID userId,
        String code,
        Instant validUntil
) {
}
