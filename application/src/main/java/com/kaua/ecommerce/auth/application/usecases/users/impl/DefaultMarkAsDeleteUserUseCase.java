package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.MarkAsDeleteUserUseCase;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.UserId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultMarkAsDeleteUserUseCase extends MarkAsDeleteUserUseCase {

    private final UserRepository userRepository;

    public DefaultMarkAsDeleteUserUseCase(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public void execute(final UserId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultMarkAsDeleteUserUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.value())
                .orElseThrow(NotFoundException.with(User.class, input));

        if (aUser.isDeleted()) return;

        final var aUserMarkAsDeleted = aUser.markAsDeleted();

        this.userRepository.update(aUserMarkAsDeleted);
    }
}
