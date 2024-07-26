package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserOutput;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.users.*;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateUserUseCase extends CreateUserUseCase {

    private final UserRepository userRepository;
    private final CryptographyGateway cryptographyGateway;
    private final RoleRepository roleRepository;

    public DefaultCreateUserUseCase(
            final UserRepository userRepository,
            final CryptographyGateway cryptographyGateway,
            final RoleRepository roleRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.cryptographyGateway = Objects.requireNonNull(cryptographyGateway);
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public CreateUserOutput execute(final CreateUserInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateUserUseCase.class.getSimpleName());

        if (this.userRepository.existsByEmail(input.email())) {
            throw DomainException.with("Email already exists");
        }

        final var aCustomerId = new CustomerId(input.customerId());
        final var aName = new UserName(input.firstName(), input.lastName());
        final var aEmail = new UserEmail(input.email());

        final var aPlainPassword = UserPassword.create(input.password());
        final var aPasswordCrypt = new UserPassword(this.cryptographyGateway.encrypt(aPlainPassword.value()));

        final var aDefaultRoles = this.roleRepository.getDefaultRoles()
                .stream().map(Role::getId)
                .collect(Collectors.toSet());

        final var aUser = User.newUser(
                aCustomerId,
                aName,
                aEmail,
                aPasswordCrypt,
                aDefaultRoles
        );

        return new CreateUserOutput(this.userRepository.save(aUser));
    }
}
