package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.UpdateRoleUseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.UpdateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.UpdateRoleOutput;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultUpdateRoleUseCase extends UpdateRoleUseCase {

    private final RoleRepository roleRepository;

    public DefaultUpdateRoleUseCase(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public UpdateRoleOutput execute(final UpdateRoleInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultUpdateRoleUseCase.class.getSimpleName());

        if (this.roleRepository.existsByName(input.name())) {
            throw DomainException.with("Role name already exists");
        }

        final var aRole = this.roleRepository.findById(input.getId())
                .orElseThrow(NotFoundException.with(Role.class, input.roleId()));

        final var aRoleName = input.name() != null
                ? new RoleName(input.name())
                : aRole.getName();
        final var aRoleDescription = input.description() != null
                ? new RoleDescription(input.description())
                : aRole.getDescription();
        final var aRoleIsDefault = input.isDefault() != null
                ? input.isDefault()
                : aRole.isDefault();

        final var aRoleUpdated = aRole.update(aRoleName, aRoleDescription, aRoleIsDefault);
        return new UpdateRoleOutput(this.roleRepository.update(aRoleUpdated));
    }
}
