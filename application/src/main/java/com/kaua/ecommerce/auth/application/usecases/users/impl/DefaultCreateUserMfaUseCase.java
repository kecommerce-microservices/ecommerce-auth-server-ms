package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserMfaInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserMfaOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.Objects;

public class DefaultCreateUserMfaUseCase extends CreateUserMfaUseCase {

    private final UserRepository userRepository;
    private final MfaGateway mfaGateway;

    public DefaultCreateUserMfaUseCase(
            final UserRepository userRepository,
            final MfaGateway mfaGateway
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.mfaGateway = Objects.requireNonNull(mfaGateway);
    }

    @Override
    public CreateUserMfaOutput execute(final CreateUserMfaInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateUserMfaUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.userId())
                .orElseThrow(NotFoundException.with(User.class, input.userId().toString()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        final var aUserMfa = aUser.getMfa();

        final var aType = UserMfaType.of(input.type())
                .orElseThrow(() -> DomainException.with("Invalid MFA type %s".formatted(input.type())));

        final var aSecret = this.mfaGateway.generateSecret(aType);
        final var aDeviceName = input.deviceName();

        if (aDeviceName == null || aDeviceName.isBlank()) {
            throw DomainException.with(new Error("deviceName", "should not be null or empty"));
        }

        final var aUserMfaUpdated = aUserMfa.createMfaOnDevice(
                aSecret,
                input.deviceName(),
                aType
        );

        aUser.setMfa(aUserMfaUpdated);

        final var aQrCodeUrl = this.mfaGateway.generateConfirmationQrCode(
                aSecret,
                aUser.getEmail().value(),
                input.deviceName()
        );

        return new CreateUserMfaOutput(this.userRepository.update(aUser), aQrCodeUrl);
    }
}
