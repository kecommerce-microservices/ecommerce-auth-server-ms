package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

class GetRoleByIdUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultGetRoleByIdUseCase getRoleByIdUseCase;

    @Test
    void givenAValidValues_whenCallGetRoleByIdUseCase_thenReturnsRole() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();

        Mockito.when(roleRepository.findById(aRoleId)).thenReturn(Optional.of(aRole));

        final var aOutput = this.getRoleByIdUseCase.execute(aRole.getId());

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aRole.getId().value().toString(), aOutput.id());
        Assertions.assertEquals(aRole.getName().value(), aOutput.name());
        Assertions.assertEquals(aRole.getDescription().value(), aOutput.description());
        Assertions.assertEquals(aRole.isDefault(), aOutput.isDefault());
        Assertions.assertEquals(aRole.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aRole.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aRole.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aRole.getDeletedAt().orElse(null), aOutput.deletedAt());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId);
    }

    @Test
    void givenANullInput_whenCallGetRoleByIdUseCase_thenThrowsIllegalArgumentException() {
        final var expectedErrorMessage = "Input to DefaultGetRoleByIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.getRoleByIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).findById(Mockito.any());
    }

    @Test
    void givenAnNonExistsRoleId_whenCallGetRoleByIdUseCase_thenThrowsNotFoundException() {
        final var aRoleId = Fixture.Roles.defaultRole().getId();
        final var expectedErrorMessage = NotFoundException
                .with(Role.class, aRoleId.value().toString())
                .get().getMessage();

        Mockito.when(roleRepository.findById(aRoleId.value())).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getRoleByIdUseCase.execute(aRoleId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId.value());
    }
}
