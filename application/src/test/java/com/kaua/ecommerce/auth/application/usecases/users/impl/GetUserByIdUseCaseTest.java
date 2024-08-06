package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.GetUserByIdInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

class GetUserByIdUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultGetUserByIdUseCase getUserByIdUseCase;

    @Test
    void givenAValidUserId_whenCallGetUserById_thenShouldReturnUser() {
        final var aRoleDefault = Fixture.Roles.defaultRole();
        final var aRoleRandom = Fixture.Roles.randomRole();

        final var aUser = Fixture.Users.randomUser(aRoleDefault.getId());
        aUser.addRoles(Set.of(aRoleRandom.getId()));

        final var aUserId = aUser.getId();
        final var aRolesIds = Set.of(aRoleDefault.getId().value(), aRoleRandom.getId().value());

        final var aInput = new GetUserByIdInput(aUserId.value());

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.of(aUser));
        Mockito.when(roleRepository.findByIds(aRolesIds)).thenReturn(Set.of(aRoleDefault, aRoleRandom));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.getUserByIdUseCase.execute(aInput));

        Assertions.assertEquals(aUser.getId().value().toString(), aOutput.userId());
        Assertions.assertEquals(aUser.getCustomerId().value().toString(), aOutput.customerId());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.lastName());
        Assertions.assertEquals(aUser.getName().fullName(), aOutput.fullName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.email());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.emailVerified());
        Assertions.assertEquals(aUser.getMfa().getId().value().toString(), aOutput.mfa().mfaId());
        Assertions.assertEquals(aUser.getMfa().isMfaEnabled(), aOutput.mfa().mfaEnabled());
        Assertions.assertEquals(aUser.getMfa().isMfaVerified(), aOutput.mfa().mfaVerified());
        Assertions.assertEquals(aUser.getMfa().getDeviceName().orElse(null), aOutput.mfa().deviceName());
        Assertions.assertEquals(aUser.getMfa().isDeviceVerified(), aOutput.mfa().deviceVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aUser.getDeletedAt().orElse(null), aOutput.deletedAt());
        Assertions.assertEquals(aUser.getVersion(), aOutput.version());

        final var aRoles = aOutput.roles();
        Assertions.assertEquals(2, aRoles.size());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId.value());
        Mockito.verify(roleRepository, Mockito.times(1)).findByIds(aRolesIds);
    }

    @Test
    void givenANullInput_whenCallGetUserById_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultGetUserByIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.getUserByIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnInvalidUserId_whenCallGetUserById_thenShouldThrowNotFoundException() {
        final var aUserId = IdentifierUtils.generateNewUUID();
        final var expectedErrorMessage = "User with id " + aUserId + " was not found";

        final var aInput = new GetUserByIdInput(aUserId);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getUserByIdUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(roleRepository, Mockito.never()).findByIds(Mockito.any());
    }
}
