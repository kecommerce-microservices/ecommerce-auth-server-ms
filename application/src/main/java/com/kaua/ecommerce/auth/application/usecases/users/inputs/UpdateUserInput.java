package com.kaua.ecommerce.auth.application.usecases.users.inputs;

import java.util.Optional;
import java.util.UUID;

public record UpdateUserInput(
        UUID id,
        String firstName,
        String lastName,
        String email
) {

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
