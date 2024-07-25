package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.UpdateRoleInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class UpdateRoleUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultUpdateRoleUseCase updateRoleUseCase;

    @Test
    void givenAValidValues_whenCallUpdateRoleUseCase_thenReturnsRoleId() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var input = new UpdateRoleInput(aRoleId.toString(), aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(false);
        Mockito.when(roleRepository.findById(aRoleId))
                .thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateRoleUseCase.execute(input);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.roleId());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId);
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidExistsRoleName_whenCallUpdateRoleUseCase_thenThrowsDomainException() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var expectedErrorMessage = "Role name already exists";

        final var input = new UpdateRoleInput(aRoleId.toString(), aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.updateRoleUseCase.execute(input));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.never()).findById(aRoleId);
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenAnInvalidRoleId_whenCallUpdateRoleUseCase_thenThrowsNotFoundException() {
        final var aRoleId = "123e4567-e89b-12d3-a456-426614174000";
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var expectedErrorMessage = NotFoundException.with(Role.class, aRoleId)
                .get().getMessage();

        final var input = new UpdateRoleInput(aRoleId, aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(false);
        Mockito.when(roleRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateRoleUseCase.execute(input));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenANullNameInput_whenCallUpdateRoleUseCase_thenUseOldName() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();
        final String aName = null;
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var input = new UpdateRoleInput(aRoleId.toString(), aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(Mockito.any())).thenReturn(false);
        Mockito.when(roleRepository.findById(aRoleId))
                .thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateRoleUseCase.execute(input);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.roleId());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(Mockito.any());
        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId);
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenANullDescriptionInput_whenCallUpdateRoleUseCase_thenUseOldDescription() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();
        final var aName = "ADMIN";
        final String aDescription = null;
        final var aIsDefault = false;

        final var input = new UpdateRoleInput(aRoleId.toString(), aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(false);
        Mockito.when(roleRepository.findById(aRoleId))
                .thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateRoleUseCase.execute(input);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.roleId());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId);
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenANullIsDefaultInput_whenCallUpdateRoleUseCase_thenUseOldIsDefault() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId().value();
        final var aName = "ADMIN";
        final var aDescription = "Administrator";
        final Boolean aIsDefault = null;

        final var input = new UpdateRoleInput(aRoleId.toString(), aName, aDescription, aIsDefault);

        Mockito.when(roleRepository.existsByName(aName)).thenReturn(false);
        Mockito.when(roleRepository.findById(aRoleId))
                .thenReturn(Optional.of(aRole));
        Mockito.when(roleRepository.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateRoleUseCase.execute(input);

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.roleId());

        Mockito.verify(roleRepository, Mockito.times(1)).existsByName(aName);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(aRoleId);
        Mockito.verify(roleRepository, Mockito.times(1)).update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallUpdateRoleUseCase_thenThrowsUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateRoleUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.updateRoleUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).existsByName(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).update(Mockito.any());
    }
}
