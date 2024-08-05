package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class MarkAsDeleteUserUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultMarkAsDeleteUserUseCase markAsDeleteUserUseCase;

    @Test
    void givenAValidUserId_whenCallMarkAsDeleteUserUseCase_thenShouldMarkAsDeleted() {
        final var aRoleId = new RoleId(UUID.randomUUID());
        final var aUser = Fixture.Users.randomUser(aRoleId);
        final var aUserId = aUser.getId();

        Mockito.when(userRepository.findById(aUserId.value()))
                .thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(Mockito.any()))
                .thenAnswer(returnsFirstArg());

        Assertions.assertDoesNotThrow(() -> this.markAsDeleteUserUseCase.execute(aUserId));

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId.value());
        Mockito.verify(userRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenAnNullInput_whenCallMarkAsDeleteUserUseCase_thenShouldThrowAnUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultMarkAsDeleteUserUseCase cannot be null";

        final var exception = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.markAsDeleteUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void givenAnDeletedUser_whenCallMarkAsDeleteUserUseCase_thenShouldNotUpdateUser() {
        final var aRoleId = new RoleId(UUID.randomUUID());
        final var aUser = Fixture.Users.randomUser(aRoleId).markAsDeleted();

        Mockito.when(userRepository.findById(aUser.getId().value()))
                .thenReturn(Optional.of(aUser));

        Assertions.assertDoesNotThrow(() -> this.markAsDeleteUserUseCase.execute(aUser.getId()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(aUser.getId().value());
        Mockito.verify(userRepository, Mockito.never()).update(aUser.markAsDeleted());
    }
}
