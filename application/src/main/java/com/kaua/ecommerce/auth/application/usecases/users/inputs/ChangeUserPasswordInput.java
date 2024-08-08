package com.kaua.ecommerce.auth.application.usecases.users.inputs;

public record ChangeUserPasswordInput(
        String token,
        String password
) {
}
