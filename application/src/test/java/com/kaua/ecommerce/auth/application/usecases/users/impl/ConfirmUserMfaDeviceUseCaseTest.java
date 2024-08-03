package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserMfaDeviceInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
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

class ConfirmUserMfaDeviceUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MfaGateway mfaGateway;

    @InjectMocks
    private DefaultConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase;

    @Test
    void givenAValidValues_whenCallConfirmUserMfaDeviceUseCase_thenShouldReturnUserId() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.getMfa().createMfaOnDevice("123456", "my-device", UserMfaType.TOTP);

        final var aUserId = aUser.getId().value();
        final var aCode = "123";
        final var aValidUntil = InstantUtils.now().plus(30, ChronoUnit.MINUTES);

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, aValidUntil);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(mfaGateway.accepts(UserMfaType.TOTP, aCode, "123456")).thenReturn(true);
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(aUserId.toString(), aOutput.userId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.times(1)).accepts(UserMfaType.TOTP, aCode, "123456");
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                cmd.getMfa().getValidUntil().isPresent()
                        && cmd.getMfa().isDeviceVerified()));
    }

    @Test
    void givenAInvalidCode_whenCallConfirmUserMfaDeviceUseCase_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.getMfa().createMfaOnDevice("123456", "my-device", UserMfaType.TOTP);

        final var aUserId = aUser.getId().value();
        final var aCode = "123";
        final var aValidUntil = InstantUtils.now().plus(30, ChronoUnit.MINUTES);

        final var expectedErrorMessage = "Invalid MFA code";

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, aValidUntil);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(mfaGateway.accepts(UserMfaType.TOTP, aCode, "123456")).thenReturn(false);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.times(1)).accepts(UserMfaType.TOTP, aCode, "123456");
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallConfirmUserMfaDeviceUseCase_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultConfirmUserMfaDeviceUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.confirmUserMfaDeviceUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(mfaGateway, Mockito.never()).accepts(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnInvalidUserId_whenCallConfirmUserMfaDeviceUseCase_thenShouldThrowNotFoundException() {
        final var aUserId = IdentifierUtils.generateNewUUID();
        final var aCode = "123";
        final var aValidUntil = InstantUtils.now().plus(30, ChronoUnit.MINUTES);

        final var expectedErrorMessage = "User with id %s was not found".formatted(aUserId);

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, aValidUntil);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.never()).accepts(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnInvalidMfaType_whenCallConfirmUserMfaDeviceUseCase_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));

        final var aUserId = aUser.getId().value();
        final var aCode = "123";
        final var aValidUntil = InstantUtils.now().plus(30, ChronoUnit.MINUTES);

        final var expectedErrorMessage = "MFA type not found";

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, aValidUntil);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.never()).accepts(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnInvalidMfaSecret_whenCallConfirmUserMfaDeviceUseCase_thenShouldThrowDomainException() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.getMfa().createMfaOnDevice(null, "my-device", UserMfaType.TOTP);

        final var aUserId = aUser.getId().value();
        final var aCode = "123";
        final var aValidUntil = InstantUtils.now().plus(30, ChronoUnit.MINUTES);

        final var expectedErrorMessage = "MFA secret not found";

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, aValidUntil);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.never()).accepts(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnInvalidValidUntil_whenCallConfirmUserMfaDeviceUseCase_thenShouldReturnUserId() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.getMfa().createMfaOnDevice("123456", "my-device", UserMfaType.TOTP);

        final var aUserId = aUser.getId().value();
        final var aCode = "123";

        final var aInput = new ConfirmUserMfaDeviceInput(aUserId, aCode, null);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(mfaGateway.accepts(UserMfaType.TOTP, aCode, "123456")).thenReturn(true);
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.confirmUserMfaDeviceUseCase.execute(aInput));

        Assertions.assertEquals(aUserId.toString(), aOutput.userId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(mfaGateway, Mockito.times(1)).accepts(UserMfaType.TOTP, aCode, "123456");
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                cmd.getMfa().getValidUntil().isPresent()
                        && cmd.getMfa().getValidUntil().get().isAfter(InstantUtils.now())
                        && cmd.getMfa().isDeviceVerified()));
    }
}
