package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.RemoveUserRoleUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.RemoveUserRoleInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.RemoveUserRoleOutput;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultRemoveUserRoleUseCase extends RemoveUserRoleUseCase {

    private final UserRepository userRepository;

    public DefaultRemoveUserRoleUseCase(final UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public RemoveUserRoleOutput execute(final RemoveUserRoleInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultRemoveUserRoleUseCase.class.getSimpleName());

        final var aUser = this.userRepository.findById(input.id())
                .orElseThrow(NotFoundException.with(User.class, input.id().toString()));

        if (aUser.isDeleted()) {
            throw new UserIsDeletedException(aUser.getId().value().toString());
        }

        aUser.removeRole(new RoleId(input.roleId()));

        return new RemoveUserRoleOutput(this.userRepository.update(aUser));
    }
}
