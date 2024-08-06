package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.GetUserByIdUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.GetUserByIdInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.GetUserByIdOutput;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultGetUserByIdUseCase extends GetUserByIdUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DefaultGetUserByIdUseCase(
            final UserRepository userRepository,
            final RoleRepository roleRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public GetUserByIdOutput execute(final GetUserByIdInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultGetUserByIdUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.id())
                .orElseThrow(NotFoundException.with(User.class, input.id().toString()));

        final var aRolesIds = aUser.getRoles()
                .stream().map(RoleId::value)
                .collect(Collectors.toSet());

        final var aRoles = this.roleRepository.findByIds(aRolesIds)
                .stream().map(it -> new GetUserByIdOutput.GetUserByIdRolesOutput(
                        it.getId().value().toString(),
                        it.getName().value()
                ))
                .collect(Collectors.toSet());

        return new GetUserByIdOutput(aUser, aRoles);
    }
}
