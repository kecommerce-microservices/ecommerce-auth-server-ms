package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.CreateRoleInput;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CreateRoleUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultCreateRoleUseCase createRoleUseCase;

    @Test
    void givenAValidValues_whenCallCreateRoleUseCase_thenReturnsRoleId() {
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var input = new CreateRoleInput(aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(false);
        Mockito.when(roleRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createRoleUseCase.execute(input);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.roleId());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidExistsRoleName_whenCallCreateRoleUseCase_thenThrowsDomainException() {
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var expectedErrorMessage = "Role already exists";

        final var input = new CreateRoleInput(aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createRoleUseCase.execute(input));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallCreateRoleUseCase_thenThrowsIllegalArgumentException() {
        final var expectedErrorMessage = "Input to DefaultCreateRoleUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.createRoleUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).existsByName(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenANullNameInput_whenCallCreateRoleUseCase_thenThrowsDomainException() {
        final String aName = null;
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "should not be empty";

        final var input = new CreateRoleInput(aName, aDescription, aIsDefault);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createRoleUseCase.execute(input));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }
}
