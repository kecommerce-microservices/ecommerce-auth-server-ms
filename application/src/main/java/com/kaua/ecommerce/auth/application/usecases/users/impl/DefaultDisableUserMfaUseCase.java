package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.DisableUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.DisableUserMfaInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.DisableUserMfaOutput;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultDisableUserMfaUseCase extends DisableUserMfaUseCase {

    private final UserRepository userRepository;

    public DefaultDisableUserMfaUseCase(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public DisableUserMfaOutput execute(final DisableUserMfaInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultDisableUserMfaUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.userId())
                .orElseThrow(NotFoundException.with(User.class, input.userId().toString()));

        final var aUserMfa = aUser.getMfa();
        final var aUserMfaDisabled = aUserMfa.disableMfa();

        aUser.setMfa(aUserMfaDisabled);

        return new DisableUserMfaOutput(this.userRepository.save(aUser));
    }
}
