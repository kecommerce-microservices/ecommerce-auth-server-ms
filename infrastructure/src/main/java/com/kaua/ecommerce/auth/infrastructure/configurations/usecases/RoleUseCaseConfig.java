package com.kaua.ecommerce.auth.infrastructure.configurations.usecases;

import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.*;
import com.kaua.ecommerce.auth.application.usecases.roles.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RoleUseCaseConfig {

    private final RoleRepository roleRepository;

    public RoleUseCaseConfig(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Bean
    public CreateRoleUseCase createRoleUseCase() {
        return new DefaultCreateRoleUseCase(roleRepository);
    }

    @Bean
    public UpdateRoleUseCase updateRoleUseCase() {
        return new DefaultUpdateRoleUseCase(roleRepository);
    }

    @Bean
    public MarkAsDeleteRoleUseCase markAsDeleteRoleUseCase() {
        return new DefaultMarkAsDeleteRoleUseCase(roleRepository);
    }

    @Bean
    public GetRoleByIdUseCase getRoleByIdUseCase() {
        return new DefaultGetRoleByIdUseCase(roleRepository);
    }

    @Bean
    public ListRolesUseCase listRolesUseCase() {
        return new DefaultListRolesUseCase(roleRepository);
    }

    @Bean
    public GetDefaultRolesUseCase getDefaultRolesUseCase() {
        return new DefaultGetDefaultRolesUseCase(roleRepository);
    }
}
