package com.kaua.ecommerce.auth.infrastructure.configurations.usecases;

import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.ConfirmUserMfaDeviceUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.DisableUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.impl.DefaultConfirmUserMfaDeviceUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.impl.DefaultCreateUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.impl.DefaultCreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.impl.DefaultDisableUserMfaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class UserUseCaseConfig {

    private final UserRepository userRepository;
    private final CryptographyGateway cryptographyGateway;
    private final RoleRepository roleRepository;
    private final MfaGateway mfaGateway;

    public UserUseCaseConfig(
            final UserRepository userRepository,
            final CryptographyGateway cryptographyGateway,
            final RoleRepository roleRepository,
            final MfaGateway mfaGateway
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.cryptographyGateway = Objects.requireNonNull(cryptographyGateway);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.mfaGateway = Objects.requireNonNull(mfaGateway);
    }

    @Bean
    public CreateUserUseCase createUserUseCase() {
        return new DefaultCreateUserUseCase(userRepository, cryptographyGateway, roleRepository);
    }

    @Bean
    public CreateUserMfaUseCase createUserMfaUseCase() {
        return new DefaultCreateUserMfaUseCase(userRepository, mfaGateway);
    }

    @Bean
    public ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase() {
        return new DefaultConfirmUserMfaDeviceUseCase(userRepository, mfaGateway);
    }

    @Bean
    public DisableUserMfaUseCase disableUserMfaUseCase() {
        return new DefaultDisableUserMfaUseCase(userRepository);
    }
}
