package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.CreateRoleUseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.CreateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.CreateRoleOutput;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

import java.util.Objects;

public class DefaultCreateRoleUseCase extends CreateRoleUseCase {

    private final RoleRepository roleRepository;

    public DefaultCreateRoleUseCase(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public CreateRoleOutput execute(final CreateRoleInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateRoleUseCase.class.getSimpleName());

        if (this.roleRepository.existsByName(input.name())) {
            throw DomainException.with("Role already exists");
        }

        final var aRoleName = new RoleName(input.name());
        final var aRoleDescription = new RoleDescription(input.description());

        final var aRole = Role.create(aRoleName, aRoleDescription, input.isDefault());
        return new CreateRoleOutput(this.roleRepository.save(aRole));
    }
}
