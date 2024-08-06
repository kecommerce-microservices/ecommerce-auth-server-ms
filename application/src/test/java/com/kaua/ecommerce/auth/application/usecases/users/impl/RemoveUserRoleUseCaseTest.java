package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.RemoveUserRoleInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class RemoveUserRoleUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultRemoveUserRoleUseCase removeUserRoleUseCase;

    @Test
    void givenAValidUserIdAndRoleId_whenCallRemoveUserRole_thenShouldRemoveUserRole() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        aUser.addRoles(Set.of(aRoleOne.getId()));
        final var aUserId = aUser.getId();

        final var aRoleId = aRoleOne.getId().value();

        final var aInput = new RemoveUserRoleInput(aUserId.value(), aRoleId);

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = this.removeUserRoleUseCase.execute(aInput);

        Assertions.assertEquals(aOutput.userId(), aUserId.value().toString());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId.value());
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId(), aUserId)
                        && !cmd.getRoles().contains(aRoleOne.getId()))
        );
    }

    @Test
    void givenAnNullInput_whenCallRemoveUserRole_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultRemoveUserRoleUseCase cannot be null";

        final var exception = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.removeUserRoleUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void givenAnInvalidUserId_whenCallRemoveUserRole_thenShouldThrowNotFoundException() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        aUser.addRoles(Set.of(aRoleOne.getId()));
        final var aUserId = aUser.getId();

        final var aRoleId = aRoleOne.getId().value();

        final var aInput = new RemoveUserRoleInput(aUserId.value(), aRoleId);

        final var expectedErrorMessage = "User with id " + aUserId.value() + " was not found";

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.empty());

        final var exception = Assertions.assertThrows(NotFoundException.class,
                () -> this.removeUserRoleUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void givenAnDeletedUser_whenCallRemoveUserRole_thenShouldThrowUserIsDeletedException() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        aUser.addRoles(Set.of(aRoleOne.getId()));
        final var aUserId = aUser.getId();

        final var aRoleId = aRoleOne.getId().value();

        final var aInput = new RemoveUserRoleInput(aUserId.value(), aRoleId);

        final var expectedErrorMessage = "User with id " + aUserId.value() + " is deleted";

        aUser.markAsDeleted();

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.of(aUser));

        final var exception = Assertions.assertThrows(UserIsDeletedException.class,
                () -> this.removeUserRoleUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
