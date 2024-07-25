package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.GetDefaultRolesUseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetDefaultRolesOutput;

import java.util.List;

public class DefaultGetDefaultRolesUseCase extends GetDefaultRolesUseCase {

    private final RoleRepository roleRepository;

    public DefaultGetDefaultRolesUseCase(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<GetDefaultRolesOutput> execute() {
        return roleRepository.getDefaultRoles()
                .stream().map(GetDefaultRolesOutput::new)
                .toList();
    }
}
