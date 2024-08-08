package com.kaua.ecommerce.auth.infrastructure.configurations.usecases;

import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.*;
import com.kaua.ecommerce.auth.application.usecases.users.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class UserUseCaseConfig {

    private final UserRepository userRepository;
    private final CryptographyGateway cryptographyGateway;
    private final RoleRepository roleRepository;
    private final MfaGateway mfaGateway;
    private final MailRepository mailRepository;

    public UserUseCaseConfig(
            final UserRepository userRepository,
            final CryptographyGateway cryptographyGateway,
            final RoleRepository roleRepository,
            final MfaGateway mfaGateway,
            final MailRepository mailRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.cryptographyGateway = Objects.requireNonNull(cryptographyGateway);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.mfaGateway = Objects.requireNonNull(mfaGateway);
        this.mailRepository = Objects.requireNonNull(mailRepository);
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

    @Bean
    public UpdateUserUseCase updateUserUseCase() {
        return new DefaultUpdateUserUseCase(userRepository);
    }

    @Bean
    public MarkAsDeleteUserUseCase markAsDeleteUserUseCase() {
        return new DefaultMarkAsDeleteUserUseCase(userRepository);
    }

    @Bean
    public AddRolesToUserUseCase addRolesToUserUseCase() {
        return new DefaultAddRolesToUserUseCase(userRepository, roleRepository);
    }

    @Bean
    public RemoveUserRoleUseCase removeUserRoleUseCase() {
        return new DefaultRemoveUserRoleUseCase(userRepository);
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase() {
        return new DefaultGetUserByIdUseCase(userRepository, roleRepository);
    }

    @Bean
    public CreateMailTokenUseCase createMailTokenUseCase() {
        return new DefaultCreateMailTokenUseCase(mailRepository, userRepository);
    }

    @Bean
    public ConfirmUserEmailUseCase confirmUserEmailUseCase() {
        return new DefaultConfirmUserEmailUseCase(userRepository, mailRepository);
    }

    @Bean
    public ChangeUserPasswordUseCase changeUserPasswordUseCase() {
        return new DefaultChangeUserPasswordUseCase(userRepository, mailRepository, cryptographyGateway);
    }
}
