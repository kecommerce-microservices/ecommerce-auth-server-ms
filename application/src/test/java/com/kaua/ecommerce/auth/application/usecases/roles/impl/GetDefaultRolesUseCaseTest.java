package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetDefaultRolesOutput;
import com.kaua.ecommerce.auth.domain.Fixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

class GetDefaultRolesUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultGetDefaultRolesUseCase getDefaultRolesUseCase;

    @Test
    void givenAValidRolesPersisted_whenCallGetDefaultRolesUseCase_thenReturnsRoles() {
        final var aRoles = List.of(Fixture.Roles.defaultRole());
        final var aRolesOutput = aRoles.stream().map(GetDefaultRolesOutput::new).toList();

        Mockito.when(roleRepository.getDefaultRoles()).thenReturn(aRoles);

        final var aOutput = this.getDefaultRolesUseCase.execute();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aRolesOutput.size(), aOutput.size());
        Assertions.assertEquals(aRolesOutput.get(0).id(), aOutput.get(0).id());
        Assertions.assertEquals(aRolesOutput.get(0).name(), aOutput.get(0).name());
        Assertions.assertEquals(aRolesOutput.get(0).description(), aOutput.get(0).description());
        Assertions.assertEquals(aRolesOutput.get(0).isDefault(), aOutput.get(0).isDefault());
        Assertions.assertEquals(aRolesOutput.get(0).isDeleted(), aOutput.get(0).isDeleted());
        Assertions.assertEquals(aRolesOutput.get(0).createdAt(), aOutput.get(0).createdAt());
        Assertions.assertEquals(aRolesOutput.get(0).updatedAt(), aOutput.get(0).updatedAt());

        Mockito.verify(roleRepository, Mockito.times(1)).getDefaultRoles();
    }
}