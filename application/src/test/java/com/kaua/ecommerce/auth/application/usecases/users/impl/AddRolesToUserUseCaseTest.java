package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.AddRolesToUserInput;
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

class AddRolesToUserUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultAddRolesToUserUseCase addRolesToUserUseCase;

    @Test
    void givenAValidUserIdAndRolesIds_whenCallAddRolesToUser_thenShouldAddRolesToUser() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aRoleTwo = Fixture.Roles.randomRole();

        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        final var aUserId = aUser.getId();

        final var aRolesIds = Set.of(aRoleOne.getId().value(), aRoleTwo.getId().value());

        final var aInput = new AddRolesToUserInput(aUserId.value(), aRolesIds);

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.of(aUser));
        Mockito.when(roleRepository.findByIds(aRolesIds)).thenReturn(Set.of(aRoleOne, aRoleTwo));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = this.addRolesToUserUseCase.execute(aInput);

        Assertions.assertEquals(aOutput.userId(), aUserId.value().toString());

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId.value());
        Mockito.verify(roleRepository, Mockito.times(1)).findByIds(aRolesIds);
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId(), aUserId)
                        && cmd.getRoles().contains(aRoleOne.getId())
                        && cmd.getRoles().contains(aRoleTwo.getId()))
        );
    }

    @Test
    void givenAnNullInput_whenCallAddRolesToUser_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultAddRolesToUserUseCase cannot be null";

        final var exception = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.addRolesToUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void givenAnInvalidUserId_whenCallAddRolesToUser_thenShouldThrowNotFoundException() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        final var aUserId = aUser.getId();

        final var aRolesIds = Set.of(aRoleOne.getId().value());

        final var aInput = new AddRolesToUserInput(aUserId.value(), aRolesIds);

        final var expectedErrorMessage = "User with id " + aUserId.value() + " was not found";

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.empty());

        final var exception = Assertions.assertThrows(NotFoundException.class,
                () -> this.addRolesToUserUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void givenAnDeletedUser_whenCallAddRolesToUser_thenShouldThrowUserIsDeletedException() {
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());
        final var aUserId = aUser.getId();

        final var aRolesIds = Set.of(aRoleOne.getId().value());

        final var aInput = new AddRolesToUserInput(aUserId.value(), aRolesIds);

        final var expectedErrorMessage = "User with id " + aUserId.value() + " is deleted";

        aUser.markAsDeleted();

        Mockito.when(userRepository.findById(aUserId.value())).thenReturn(Optional.of(aUser));

        final var exception = Assertions.assertThrows(UserIsDeletedException.class,
                () -> this.addRolesToUserUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
