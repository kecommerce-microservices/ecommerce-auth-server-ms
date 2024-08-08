package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ChangeUserPasswordInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
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
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class ChangeUserPasswordUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailRepository mailRepository;

    @Mock
    private CryptographyGateway cryptographyGateway;

    @InjectMocks
    private DefaultChangeUserPasswordUseCase changeUserPasswordUseCase;

    @Test
    void givenAValidTokenAndPassword_whenCallChangeUserPassword_thenShouldReturnUserId() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.PASSWORD_RESET
        );

        final var aPassword = "Ab*C12345";

        final var aInput = new ChangeUserPasswordInput(aMail.getToken(), aPassword);

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.of(aUser));
        Mockito.when(cryptographyGateway.encrypt(aPassword))
                .thenReturn(aPassword);
        Mockito.when(userRepository.update(Mockito.any()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(mailRepository.update(Mockito.any()))
                .thenAnswer(returnsFirstArg());

        final var aOutput = this.changeUserPasswordUseCase.execute(aInput);

        Assertions.assertEquals(aUser.getId().value().toString(), aOutput.userId());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(cryptographyGateway, Mockito.times(1))
                .encrypt(aPassword);
        Mockito.verify(userRepository, Mockito.times(1))
                .update(argThat(cmd -> Objects.equals(cmd.getPassword().value(), aPassword)));
        Mockito.verify(mailRepository, Mockito.times(1))
                .update(argThat(MailToken::isUsed));
    }

    @Test
    void givenAnExpiredToken_whenCallChangeUserPassword_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = MailToken.newMailToken(
                aUser.getEmail().value(),
                aUser.getId(),
                IdentifierUtils.generateNewIdWithoutHyphen(),
                MailType.PASSWORD_RESET,
                InstantUtils.now().minus(10, ChronoUnit.MINUTES)
        );

        final var expectedErrorMessage = "Mail token is expired";

        final var aPassword = "Ab*C12345";

        final var aInput = new ChangeUserPasswordInput(aMail.getToken(), aPassword);

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.changeUserPasswordUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.never())
                .findById(aMail.getUserId().value());
        Mockito.verify(cryptographyGateway, Mockito.never())
                .encrypt(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenADeletedUser_whenCallChangeUserPassword_thenShouldThrowUserIsDeletedException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.markAsDeleted();

        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.PASSWORD_RESET
        );

        final var expectedErrorMessage = "User with id %s is deleted".formatted(aUser.getId().value().toString());

        final var aPassword = "Ab*C12345";

        final var aInput = new ChangeUserPasswordInput(aMail.getToken(), aPassword);

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(UserIsDeletedException.class,
                () -> this.changeUserPasswordUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(cryptographyGateway, Mockito.never())
                .encrypt(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallChangeUserPassword_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultChangeUserPasswordUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.changeUserPasswordUseCase.execute(null)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.never())
                .findByToken(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.never())
                .findById(Mockito.any());
        Mockito.verify(cryptographyGateway, Mockito.never())
                .encrypt(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenAnInvalidToken_whenCallChangeUserPassword_thenShouldThrowNotFoundException() {
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var expectedErrorMessage = "MailToken with id %s was not found".formatted(aToken);

        final var aPassword = "Ab*C12345";

        final var aInput = new ChangeUserPasswordInput(aToken, aPassword);

        Mockito.when(mailRepository.findByToken(aToken))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.changeUserPasswordUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aToken);
        Mockito.verify(userRepository, Mockito.never())
                .findById(Mockito.any());
        Mockito.verify(cryptographyGateway, Mockito.never())
                .encrypt(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }

    @Test
    void givenAnInvalidUserId_whenCallChangeUserPassword_thenShouldThrowNotFoundException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        final var aMail = Fixture.Mails.mail(
                aUser.getEmail().value(),
                aUser.getId().value().toString(),
                MailType.PASSWORD_RESET
        );
        final var expectedErrorMessage = "User with id %s was not found".formatted(aMail.getUserId().value());

        final var aPassword = "Ab*C12345";

        final var aInput = new ChangeUserPasswordInput(aMail.getToken(), aPassword);

        Mockito.when(mailRepository.findByToken(aMail.getToken()))
                .thenReturn(Optional.of(aMail));
        Mockito.when(userRepository.findById(aMail.getUserId().value()))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.changeUserPasswordUseCase.execute(aInput)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(mailRepository, Mockito.times(1))
                .findByToken(aMail.getToken());
        Mockito.verify(userRepository, Mockito.times(1))
                .findById(aMail.getUserId().value());
        Mockito.verify(cryptographyGateway, Mockito.never())
                .encrypt(Mockito.any());
        Mockito.verify(userRepository, Mockito.never())
                .update(Mockito.any());
        Mockito.verify(mailRepository, Mockito.never())
                .update(Mockito.any());
    }
}
