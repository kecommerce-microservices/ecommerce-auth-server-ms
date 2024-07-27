package com.kaua.ecommerce.auth.infrastructure.configurations.usecases;

import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.impl.DefaultCreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class UserUseCaseConfig {

    private final UserRepository userRepository;
    private final CryptographyGateway cryptographyGateway;
    private final RoleRepository roleRepository;

    public UserUseCaseConfig(
            final UserRepository userRepository,
            final CryptographyGateway cryptographyGateway,
            final RoleRepository roleRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.cryptographyGateway = Objects.requireNonNull(cryptographyGateway);
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Bean
    public CreateUserUseCase createUserUseCase() {
        return new DefaultCreateUserUseCase(userRepository, cryptographyGateway, roleRepository);
    }
}
