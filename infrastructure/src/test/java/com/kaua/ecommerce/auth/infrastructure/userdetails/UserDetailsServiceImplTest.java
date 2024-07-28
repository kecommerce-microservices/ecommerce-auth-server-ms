package com.kaua.ecommerce.auth.infrastructure.userdetails;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.infrastructure.IntegrationTest;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserJpaEntityRepository userJpaEntityRepository;

    @Autowired
    private RoleJpaEntityRepository roleJpaEntityRepository;

    @Test
    void givenAValidEmail_whenLoadUserByUsername_thenShouldReturnUserDetails() {
        final var aRoleDefault = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleDefault));

        final var aUser = Fixture.Users.randomUser(aRoleDefault.getId());
        final var aUserEmail = aUser.getEmail().value();

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());
        Assertions.assertEquals(1, this.roleJpaEntityRepository.count());

        final var aUserDetailsService = new UserDetailsServiceImpl(
                userJpaEntityRepository,
                roleJpaEntityRepository
        );

        final var aUserDetails = Assertions.assertDoesNotThrow(() ->
                aUserDetailsService.loadUserByUsername(aUserEmail));

        Assertions.assertEquals(aUser.getId().value().toString(), aUserDetails.getUsername());
        Assertions.assertEquals(aUser.getPassword().value(), aUserDetails.getPassword());
        Assertions.assertEquals(1, aUserDetails.getAuthorities().size());
    }

    @Test
    void givenAnNonExistsUser_whenLoadUserByUsername_thenShouldThrowNotFoundException() {
        final var aUserDetailsService = new UserDetailsServiceImpl(
                userJpaEntityRepository,
                roleJpaEntityRepository
        );

        final var aUserEmail = Fixture.Users.email();

        final var expectedMessage = "User with id " + aUserEmail + " was not found";

        final var aException = Assertions.assertThrows(
                NotFoundException.class,
                () -> aUserDetailsService.loadUserByUsername(aUserEmail)
        );

        Assertions.assertEquals(expectedMessage, aException.getMessage());
    }
}
