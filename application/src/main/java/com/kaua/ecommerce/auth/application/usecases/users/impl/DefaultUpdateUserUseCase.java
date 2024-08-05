package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.UpdateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.UpdateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.UpdateUserOutput;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.UserEmail;
import com.kaua.ecommerce.auth.domain.users.UserName;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.Optional;

public class DefaultUpdateUserUseCase extends UpdateUserUseCase {

    private final UserRepository userRepository;

    public DefaultUpdateUserUseCase(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public UpdateUserOutput execute(final UpdateUserInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultUpdateUserUseCase.class.getSimpleName());

        final var aExistsEmail = input.getEmail().map(userRepository::existsByEmail).orElse(false);

        if (aExistsEmail) {
            throw DomainException.with("Email already exists");
        }

        final var aUser = this.userRepository.findById(input.id())
                .orElseThrow(NotFoundException.with(User.class, input.id().toString()));

        final var aFirstName = input.firstName();
        final var aLastName = input.lastName();

        final var aName = aFirstName == null || aLastName == null
                ? Optional.empty()
                : Optional.of(new UserName(aFirstName, aLastName));

        aName.ifPresent(it -> aUser.changeName((UserName) it));
        input.getEmail().ifPresent(it -> aUser.changeEmail(new UserEmail(it)));

        return new UpdateUserOutput(this.userRepository.update(aUser));
    }
}
