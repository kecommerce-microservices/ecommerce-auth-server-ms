package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.GetRoleByIdUseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetRoleByIdOutput;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetRoleByIdUseCase extends GetRoleByIdUseCase {

    private final RoleRepository roleRepository;

    public DefaultGetRoleByIdUseCase(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public GetRoleByIdOutput execute(final RoleId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultGetRoleByIdUseCase.class.getSimpleName());
        return this.roleRepository.findById(input.value())
                .map(GetRoleByIdOutput::new)
                .orElseThrow(NotFoundException.with(Role.class, input.value().toString()));
    }
}
