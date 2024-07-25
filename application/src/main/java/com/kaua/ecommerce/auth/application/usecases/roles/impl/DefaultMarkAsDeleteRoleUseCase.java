package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.MarkAsDeleteRoleUseCase;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.Objects;

public class DefaultMarkAsDeleteRoleUseCase extends MarkAsDeleteRoleUseCase {

    private final RoleRepository roleRepository;

    public DefaultMarkAsDeleteRoleUseCase(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public void execute(final RoleId id) {
        if (id == null) throw new UseCaseInputCannotBeNullException(DefaultMarkAsDeleteRoleUseCase.class.getSimpleName());

        final var aRoleId = id.value();
        final var aRole = this.roleRepository.findById(aRoleId)
                .orElseThrow(NotFoundException.with(Role.class, String.valueOf(aRoleId)));

        if (aRole.isDefault() && this.roleRepository.countIsDefaultRoles() == 1) {
            throw DomainException.with(new Error("cannot delete the last default role"));
        }

        final var aRoleUpdated = aRole.markAsDeleted();

        this.roleRepository.update(aRoleUpdated);
    }
}
