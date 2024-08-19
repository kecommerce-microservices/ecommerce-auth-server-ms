package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.users.UserId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class DeleteUserUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultDeleteUserUseCase deleteUserUseCase;

    @Test
    void givenAValidUserId_whenCallDeleteUser_thenShouldDeleteUser() {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());

        Mockito.doNothing().when(userRepository).deleteByUserId(aUserId.value());

        Assertions.assertDoesNotThrow(() -> this.deleteUserUseCase.execute(aUserId));

        Mockito.verify(userRepository, Mockito.times(1)).deleteByUserId(aUserId.value());
    }

    @Test
    void givenAnInvalidNullInput_whenCallDeleteUser_thenShouldThrowUseCaseInputCannotBeNullException() {
        final UserId aUserId = null;

        final var expectedErrorMessage = "Input to DefaultDeleteUserUseCase cannot be null";

        final var exception = Assertions.assertThrows(UseCaseInputCannotBeNullException.class, () -> this.deleteUserUseCase.execute(aUserId));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
