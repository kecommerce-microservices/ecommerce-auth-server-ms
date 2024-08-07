package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateMailTokenInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CreateMailTokenUseCaseTest extends UseCaseTest {

    @Mock
    private MailRepository mailRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultCreateMailTokenUseCase createMailTokenUseCase;

    @Test
    void givenAValidValuesWithTypeIsMailConfirmation_whenCallCreateMailToken_thenShouldReturnMailToken() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aEmail = aUser.getEmail().value();
        final var aType = MailType.EMAIL_CONFIRMATION.name();

        final var aInput = new CreateMailTokenInput(aEmail, aType);

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));
        Mockito.when(mailRepository.findByEmail(aEmail)).thenReturn(List.of());
        Mockito.when(mailRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createMailTokenUseCase.execute(aInput);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUser.getId().value().toString());
        Assertions.assertEquals(aOutput.type(), aType);
        Assertions.assertNotNull(aOutput.mailId());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAValidValuesWithTypeIsMailConfirmationAndAlreadyExistsMailToken_whenCallCreateMailToken_thenShouldReturnMailToken() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aEmail = aUser.getEmail().value();
        final var aType = MailType.EMAIL_CONFIRMATION.name();

        final var aInput = new CreateMailTokenInput(aEmail, aType);

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));
        Mockito.when(mailRepository.findByEmail(aEmail)).thenReturn(List.of(Fixture.Mails.mail(
                aEmail,
                aUser.getId().value().toString(),
                MailType.EMAIL_CONFIRMATION
        )));
        Mockito.doNothing().when(mailRepository).deleteByToken(Mockito.any());
        Mockito.when(mailRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createMailTokenUseCase.execute(aInput);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUser.getId().value().toString());
        Assertions.assertEquals(aOutput.type(), aType);
        Assertions.assertNotNull(aOutput.mailId());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAValidValuesWithTypeIsPasswordReset_whenCallCreateMailToken_thenShouldReturnMailToken() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aEmail = aUser.getEmail().value();
        final var aType = MailType.PASSWORD_RESET.name();

        final var aInput = new CreateMailTokenInput(aEmail, aType);

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));
        Mockito.when(mailRepository.findByEmail(aEmail)).thenReturn(List.of());
        Mockito.when(mailRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createMailTokenUseCase.execute(aInput);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUser.getId().value().toString());
        Assertions.assertEquals(aOutput.type(), aType);
        Assertions.assertNotNull(aOutput.mailId());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAValidValuesWithTypeIsPasswordResetAndAlreadyExistsMailToken_whenCallCreateMailToken_thenShouldReturnMailToken() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aEmail = aUser.getEmail().value();
        final var aType = MailType.PASSWORD_RESET.name();

        final var aInput = new CreateMailTokenInput(aEmail, aType);

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));
        Mockito.when(mailRepository.findByEmail(aEmail)).thenReturn(List.of(Fixture.Mails.mail(
                aEmail,
                aUser.getId().value().toString(),
                MailType.PASSWORD_RESET
        )));
        Mockito.doNothing().when(mailRepository).deleteByToken(Mockito.any());
        Mockito.when(mailRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createMailTokenUseCase.execute(aInput);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUser.getId().value().toString());
        Assertions.assertEquals(aOutput.type(), aType);
        Assertions.assertNotNull(aOutput.mailId());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(1)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidType_whenCallCreateMailToken_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aEmail = aUser.getEmail().value();
        final var aType = "invalid";

        final var expectedErrorMessage = "Invalid mail type %s".formatted(aType);

        final var aInput = new CreateMailTokenInput(aEmail, aType);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createMailTokenUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallCreateMailToken_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultCreateMailTokenUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.createMailTokenUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(0)).findByEmail(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).findByEmail(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenANullEmail_whenCallCreateMailToken_thenShouldThrowNotFoundException() {
        final var aEmail = "email";

        final var expectedErrorMessage = "User with id %s was not found".formatted(aEmail);

        final var aInput = new CreateMailTokenInput(aEmail, MailType.EMAIL_CONFIRMATION.name());

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createMailTokenUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAUserIsDeleted_whenCallCreateMailToken_thenShouldThrowUserIsDeletedException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.markAsDeleted();

        final var aEmail = aUser.getEmail().value();

        final var expectedErrorMessage = "User with id %s is deleted".formatted(aUser.getId().value().toString());

        final var aInput = new CreateMailTokenInput(aEmail, MailType.EMAIL_CONFIRMATION.name());

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createMailTokenUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAUserIsEmailVerified_whenCallCreateMailTokenWithTypeIsMailConfirmation_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.confirmEmail();

        final var aEmail = aUser.getEmail().value();

        final var expectedErrorMessage = "User email is already verified";

        final var aInput = new CreateMailTokenInput(aEmail, MailType.EMAIL_CONFIRMATION.name());

        Mockito.when(userRepository.findByEmail(aEmail)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createMailTokenUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).findByEmail(aEmail);
        Mockito.verify(mailRepository, Mockito.times(0)).deleteByToken(Mockito.any());
        Mockito.verify(mailRepository, Mockito.times(0)).save(Mockito.any());
    }
}
