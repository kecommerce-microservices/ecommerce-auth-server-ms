package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.ConfirmUserMfaDeviceUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserMfaDeviceInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.ConfirmUserMfaDeviceOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DefaultConfirmUserMfaDeviceUseCase extends ConfirmUserMfaDeviceUseCase {

    private final UserRepository userRepository;
    private final MfaGateway mfaGateway;

    public DefaultConfirmUserMfaDeviceUseCase(
            final UserRepository userRepository,
            final MfaGateway mfaGateway
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.mfaGateway = Objects.requireNonNull(mfaGateway);
    }

    @Override
    public ConfirmUserMfaDeviceOutput execute(final ConfirmUserMfaDeviceInput input) {
        if (input == null)
            throw new UseCaseInputCannotBeNullException(DefaultConfirmUserMfaDeviceUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.userId())
                .orElseThrow(NotFoundException.with(User.class, input.userId().toString()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        final var aUserMfa = aUser.getMfa();

        final var aUserMfaType = aUserMfa.getMfaType()
                .orElseThrow(() -> DomainException.with("MFA type not found"));
        final var aUserMfaSecret = aUserMfa.getMfaSecret()
                .orElseThrow(() -> DomainException.with("MFA secret not found"));

        final var aAccepts = this.mfaGateway.accepts(aUserMfaType, input.code(), aUserMfaSecret);

        if (aAccepts) {
            final var aUserMfaUpdated = aUserMfa.confirmDevice(
                    input.validUntil() != null ? input.validUntil() : InstantUtils.now().plus(30, ChronoUnit.MINUTES)
            );

            aUser.setMfa(aUserMfaUpdated);
            return new ConfirmUserMfaDeviceOutput(this.userRepository.update(aUser));
        }

        throw DomainException.with("Invalid MFA code");
    }
}
