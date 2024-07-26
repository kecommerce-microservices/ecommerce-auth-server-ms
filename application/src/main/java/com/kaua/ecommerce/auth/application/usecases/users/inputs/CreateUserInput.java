package com.kaua.ecommerce.auth.application.usecases.users.inputs;

public record CreateUserInput(
        String customerId,
        String firstName,
        String lastName,
        String email,
        String password
) {
}
