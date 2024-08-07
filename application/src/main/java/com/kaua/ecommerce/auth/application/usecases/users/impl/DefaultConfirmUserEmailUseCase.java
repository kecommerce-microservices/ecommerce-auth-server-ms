package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.ConfirmUserEmailUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserEmailInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.ConfirmUserEmailOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultConfirmUserEmailUseCase extends ConfirmUserEmailUseCase {

    private final UserRepository userRepository;
    private final MailRepository mailRepository;

    public DefaultConfirmUserEmailUseCase(
            final UserRepository userRepository,
            final MailRepository mailRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.mailRepository = Objects.requireNonNull(mailRepository);
    }

    @Override
    public ConfirmUserEmailOutput execute(final ConfirmUserEmailInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultConfirmUserEmailUseCase.class.getSimpleName());

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

        final var aUserVerified = aUser.confirmEmail();
        final var aMailUsed = aMail.markAsUsed();

        final var aUpdatedUser = this.userRepository.update(aUserVerified);
        this.mailRepository.update(aMailUsed);

        return new ConfirmUserEmailOutput(aUpdatedUser);
    }
}
