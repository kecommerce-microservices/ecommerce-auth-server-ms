package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.DeleteUserUseCase;
import com.kaua.ecommerce.auth.domain.users.UserId;

import java.util.Objects;

public class DefaultDeleteUserUseCase extends DeleteUserUseCase {

    private final UserRepository userRepository;

    public DefaultDeleteUserUseCase(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public void execute(final UserId aId) {
        if (aId == null) throw new UseCaseInputCannotBeNullException(DefaultDeleteUserUseCase.class.getSimpleName());

        this.userRepository.deleteByUserId(aId.value());
    }
}
