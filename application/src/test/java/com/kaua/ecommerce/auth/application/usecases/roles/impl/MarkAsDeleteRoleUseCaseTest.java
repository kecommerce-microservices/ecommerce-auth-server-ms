package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class MarkAsDeleteRoleUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultMarkAsDeleteRoleUseCase markAsDeleteRoleUseCase;

    @Test
    void givenAValidRoleIdAndRoleIsDefault_whenCallMarkAsDeleteRoleUseCase_thenReturnsVoid() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId();

        Mockito.when(roleRepository.findById(aRoleId.value())).thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.countIsDefaultRoles()).thenReturn(2);
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        Assertions.assertDoesNotThrow(() -> this.markAsDeleteRoleUseCase.execute(aRoleId));

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId.value());
        Mockito.verify(roleRepository, Mockito.times(1)).countIsDefaultRoles();
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenAValidRoleIdAndRoleIsNotDefault_whenCallMarkAsDeleteRoleUseCase_thenReturnsVoid() {
        final var aRole = Role.create(
                new RoleName("ADMIN"),
                new RoleDescription("Administrator"),
                false
        );
        final var aRoleId = aRole.getId();

        Mockito.when(roleRepository.findById(aRoleId.value())).thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        Assertions.assertDoesNotThrow(() -> this.markAsDeleteRoleUseCase.execute(aRoleId));

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId.value());
        Mockito.verify(roleRepository, Mockito.times(0)).countIsDefaultRoles();
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenANullRoleId_whenCallMarkAsDeleteRoleUseCase_thenThrowsUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultMarkAsDeleteRoleUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.markAsDeleteRoleUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).countIsDefaultRoles();
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnNonExistsRoleId_whenCallMarkAsDeleteRoleUseCase_thenThrowsNotFoundException() {
        final var aRoleId = new RoleId(UUID.randomUUID());
        final var expectedErrorMessage = NotFoundException.with(Role.class, aRoleId.value().toString())
                .get().getMessage();

        Mockito.when(roleRepository.findById(aRoleId.value())).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.markAsDeleteRoleUseCase.execute(aRoleId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId.value());
        Mockito.verify(roleRepository, Mockito.never()).countIsDefaultRoles();
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAValidRoleIdAndRoleIsDefaultAndIsTheLastDefault_whenCallMarkAsDeleteRoleUseCase_thenThrowsDomainException() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId();
        final var expectedErrorMessage = "cannot delete the last default role";

        Mockito.when(roleRepository.findById(aRoleId.value())).thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.countIsDefaultRoles()).thenReturn(1);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.markAsDeleteRoleUseCase.execute(aRoleId));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId.value());
        Mockito.verify(roleRepository, Mockito.times(1)).countIsDefaultRoles();
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }
}
