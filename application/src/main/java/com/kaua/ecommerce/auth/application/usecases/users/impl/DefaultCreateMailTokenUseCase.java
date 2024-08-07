package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.CreateMailTokenUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateMailTokenInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateMailTokenOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DefaultCreateMailTokenUseCase extends CreateMailTokenUseCase {

    private final MailRepository mailRepository;
    private final UserRepository userRepository;

    public DefaultCreateMailTokenUseCase(
            final MailRepository mailRepository,
            final UserRepository userRepository
    ) {
        this.mailRepository = Objects.requireNonNull(mailRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public CreateMailTokenOutput execute(final CreateMailTokenInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateMailTokenUseCase.class.getSimpleName());

        final var aMailType = MailType.of(input.type())
                .orElseThrow(() -> DomainException
                        .with("Invalid mail type %s".formatted(input.type())));

        final var aUser = this.userRepository.findByEmail(input.email())
                .orElseThrow(NotFoundException.with(User.class, input.email()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        final var aMailsTokens = this.mailRepository.findByEmail(input.email());

        aMailsTokens.stream()
                .filter(it -> it.getType().name().equals(aMailType.name()))
                .forEach(it -> this.mailRepository.deleteByToken(it.getToken()));

        final var aMailToken = MailToken.newMailToken(
                aUser.getEmail().value(),
                aUser.getId(),
                IdentifierUtils.generateNewIdWithoutHyphen(),
                aMailType,
                InstantUtils.now().plus(3, ChronoUnit.DAYS)
        );

        return new CreateMailTokenOutput(this.mailRepository.save(aMailToken));
    }
}
