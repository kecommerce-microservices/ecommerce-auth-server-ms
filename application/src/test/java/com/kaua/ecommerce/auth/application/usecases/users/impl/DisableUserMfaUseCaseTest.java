package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.DisableUserMfaInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
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

public class DisableUserMfaUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultDisableUserMfaUseCase disableUserMfaUseCase;

    @Test
    void givenAValidUserId_whenCallDisableUserMfaUseCase_thenShouldReturnUserId() {
        final var aUser = Fixture.Users.randomUser(new RoleId(IdentifierUtils.generateNewUUID()));
        aUser.getMfa().createMfaOnDevice("123456", "my-device", UserMfaType.TOTP);
        aUser.getMfa().confirmDevice(InstantUtils.now().plus(30, ChronoUnit.MINUTES));
        aUser.getMfa().verifyMfa();

        final var aUserId = aUser.getId().value();

        final var aInput = new DisableUserMfaInput(aUserId);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.save(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.disableUserMfaUseCase.execute(aInput));

        Assertions.assertEquals(aUserId.toString(), aOutput.userId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.times(1)).save(argThat(cmd ->
                !cmd.getMfa().isDeviceVerified()
                        && cmd.getMfa().getValidUntil().isEmpty()));
    }

    @Test
    void givenANullInput_whenCallDisableUserMfaUseCase_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultDisableUserMfaUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.disableUserMfaUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenAnInvalidUserId_whenCallDisableUserMfaUseCase_thenShouldThrowNotFoundException() {
        final var aUserId = IdentifierUtils.generateNewUUID();

        final var expectedErrorMessage = "User with id %s was not found".formatted(aUserId);

        final var aInput = new DisableUserMfaInput(aUserId);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.disableUserMfaUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }
}
