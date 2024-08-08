package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.ChangeUserPasswordUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ChangeUserPasswordInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.ChangeUserPasswordOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.UserPassword;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultChangeUserPasswordUseCase extends ChangeUserPasswordUseCase {

    private final UserRepository userRepository;
    private final MailRepository mailRepository;
    private final CryptographyGateway cryptographyGateway;

    public DefaultChangeUserPasswordUseCase(
            final UserRepository userRepository,
            final MailRepository mailRepository,
            final CryptographyGateway cryptographyGateway
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.mailRepository = Objects.requireNonNull(mailRepository);
        this.cryptographyGateway = Objects.requireNonNull(cryptographyGateway);
    }

    @Override
    public ChangeUserPasswordOutput execute(final ChangeUserPasswordInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultChangeUserPasswordUseCase.class.getSimpleName());

        final var aMail = this.mailRepository.findByToken(input.token())
                .orElseThrow(NotFoundException.with(MailToken.class, input.token()));

        if (aMail.isExpired()) {
            throw DomainException.with("Mail token is expired");
        }

        final var aUser = this.userRepository.findById(aMail.getUserId().value())
                .orElseThrow(NotFoundException.with(User.class, aMail.getUserId()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        final var aPasswordPlainValidate = UserPassword.create(input.password());
        final var aPasswordHash = this.cryptographyGateway.encrypt(aPasswordPlainValidate.value());
        final var aUserPasswordChanged = aUser.changePassword(new UserPassword(aPasswordHash));

        final var aMailUsed = aMail.markAsUsed();

        final var aUpdatedUser = this.userRepository.update(aUserPasswordChanged);
        this.mailRepository.update(aMailUsed);

        return new ChangeUserPasswordOutput(aUpdatedUser);
    }
}
