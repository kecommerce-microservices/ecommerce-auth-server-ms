package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.AddRolesToUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.AddRolesToUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.AddRolesToUserOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultAddRolesToUserUseCase extends AddRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DefaultAddRolesToUserUseCase(
            final UserRepository userRepository,
            final RoleRepository roleRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public AddRolesToUserOutput execute(final AddRolesToUserInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultAddRolesToUserUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.id())
                .orElseThrow(NotFoundException.with(User.class, input.id().toString()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        final var aRoles = this.roleRepository.findByIds(input.rolesIds());

        final var aRolesIds = aRoles.stream().map(Role::getId)
                .collect(Collectors.toSet());

        aUser.addRoles(aRolesIds);

        return new AddRolesToUserOutput(this.userRepository.update(aUser));
    }
}
