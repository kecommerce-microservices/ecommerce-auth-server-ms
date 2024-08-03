package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserMfaInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CreateUserMfaUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MfaGateway mfaGateway;

    @InjectMocks
    private DefaultCreateUserMfaUseCase createUserMfaUseCase;

    @Test
    void givenAValidInput_whenCallCreateUserMfaUseCase_thenReturnUserIdAndQrCode() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aUserId = aUser.getId().value();
        final var aType = UserMfaType.TOTP;
        final var aDeviceName = "My Device";

        final var aInput = new CreateUserMfaInput(aUserId, aType.name(), aDeviceName);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(mfaGateway.generateSecret(aType)).thenReturn("secret");
        Mockito.when(mfaGateway.generateConfirmationQrCode("secret", aUser.getEmail().value(), aDeviceName))
                .thenReturn("qrCodeUrl");
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.createUserMfaUseCase.execute(aInput));

        Assertions.assertEquals(aUserId.toString(), aOutput.userId());
        Assertions.assertEquals("qrCodeUrl", aOutput.qrCodeUrl());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.times(1)).generateSecret(aType);
        Mockito.verify(mfaGateway, Mockito.times(1))
                .generateConfirmationQrCode("secret", aUser.getEmail().value(), aDeviceName);
        Mockito.verify(userRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallCreateUserMfaUseCase_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultCreateUserMfaUseCase cannot be null";

        final var aException = Assertions.assertThrows(
                UseCaseInputCannotBeNullException.class,
                () -> this.createUserMfaUseCase.execute(null)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnInvalidMfaType_whenCallCreateUserMfaUseCase_thenThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aUserId = aUser.getId().value();
        final var aType = "INVALID";
        final var aDeviceName = "My Device";

        final var aInput = new CreateUserMfaInput(aUserId, aType, aDeviceName);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> this.createUserMfaUseCase.execute(aInput)
        );

        Assertions.assertEquals("Invalid MFA type INVALID", aException.getMessage());
    }

    @Test
    void givenAnInvalidUserId_whenCallCreateUserMfaUseCase_thenThrowNotFoundException() {
        final var aUserId = IdentifierUtils.generateNewUUID();
        final var aType = UserMfaType.TOTP;
        final var aDeviceName = "My Device";

        final var aInput = new CreateUserMfaInput(aUserId, aType.name(), aDeviceName);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.createUserMfaUseCase.execute(aInput)
        );

        Assertions.assertEquals("User with id %s was not found".formatted(aUserId), aException.getMessage());
    }

    @Test
    void givenAnEmptyDeviceName_whenCallCreateUserMfaUseCase_thenThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aUserId = aUser.getId().value();
        final var aType = UserMfaType.TOTP;
        final var aDeviceName = "";

        final var aInput = new CreateUserMfaInput(aUserId, aType.name(), aDeviceName);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> this.createUserMfaUseCase.execute(aInput)
        );

        Assertions.assertEquals("should not be null or empty", aException.getErrors().get(0).message());
        Assertions.assertEquals("deviceName", aException.getErrors().get(0).property());
    }

    @Test
    void givenANullDeviceName_whenCallCreateUserMfaUseCase_thenThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aUserId = aUser.getId().value();
        final var aType = UserMfaType.TOTP;
        final String aDeviceName = null;

        final var aInput = new CreateUserMfaInput(aUserId, aType.name(), aDeviceName);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> this.createUserMfaUseCase.execute(aInput)
        );

        Assertions.assertEquals("should not be null or empty", aException.getErrors().get(0).message());
        Assertions.assertEquals("deviceName", aException.getErrors().get(0).property());
    }
}
