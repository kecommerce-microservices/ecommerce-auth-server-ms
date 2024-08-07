package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserEmailInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class ConfirmUserEmailUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailRepository mailRepository;

    @InjectMocks
    private DefaultConfirmUserEmailUseCase confirmUserEmailUseCase;

    @Test
    void givenAValidToken_whenCallConfirmUserEmail_thenShouldReturnUserId() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.EMAIL_CONFIRMATION
        );

        final var aInput = new ConfirmUserEmailInput(aMail.getToken());

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser.confirmEmail()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(mailRepository.update(aMail.markAsUsed()))
                .thenAnswer(returnsFirstArg());

        final var aOutput = this.confirmUserEmailUseCase.execute(aInput);

        Assertions.assertEquals(aUser.getId().value().toString(), aOutput.userId());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(userRepository, Mockito.times(1))
                .update(argThat(User::isEmailVerified));
        Mockito.verify(mailRepository, Mockito.times(1))
                .update(argThat(MailToken::isUsed));
    }

    @Test
    void givenAnExpiredToken_whenCallConfirmUserEmail_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = MailToken.newMailToken(
                aUser.getEmail().value(),
                aUser.getId(),
                IdentifierUtils.generateNewIdWithoutHyphen(),
                MailType.EMAIL_CONFIRMATION,
                InstantUtils.now().minus(10, ChronoUnit.MINUTES)
        );

        final var expectedErrorMessage = "Mail token is expired";

        final var aInput = new ConfirmUserEmailInput(aMail.getToken());

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.confirmUserEmailUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.never())
                .findById(aMail.getUserId().value());
        Mockito.verify(userRepository, Mockito.never())
                .update(argThat(User::isEmailVerified));
        Mockito.verify(mailRepository, Mockito.never())
                .update(argThat(MailToken::isUsed));
    }

    @Test
    void givenADeletedUser_whenCallConfirmUserEmail_thenShouldThrowUserIsDeletedException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.markAsDeleted();

        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.EMAIL_CONFIRMATION
        );

        final var expectedErrorMessage = "User with id %s is deleted".formatted(aUser.getId().value().toString());

        final var aInput = new ConfirmUserEmailInput(aMail.getToken());

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(UserIsDeletedException.class,
                () -> this.confirmUserEmailUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallConfirmUserEmail_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultConfirmUserEmailUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.confirmUserEmailUseCase.execute(null)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.never())
                .findByToken(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never())
                .findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenAnInvalidToken_whenCallConfirmUserEmail_thenShouldThrowNotFoundException() {
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var expectedErrorMessage = "MailToken with id %s was not found".formatted(aToken);

        final var aInput = new ConfirmUserEmailInput(aToken);

        Mockito.when(mailRepository.findByToken(aToken))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.confirmUserEmailUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aToken);
        Mockito.verify(userRepository, Mockito.never())
                .findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenAnInvalidUserId_whenCallConfirmUserEmail_thenShouldThrowNotFoundException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.EMAIL_CONFIRMATION
        );
        final var expectedErrorMessage = "User with id %s was not found".formatted(aMail.getUserId().value());

        final var aInput = new ConfirmUserEmailInput(aMail.getToken());

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.confirmUserEmailUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }
}
