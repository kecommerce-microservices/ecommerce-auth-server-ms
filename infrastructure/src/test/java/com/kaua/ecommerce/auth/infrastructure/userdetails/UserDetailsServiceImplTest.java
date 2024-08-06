package com.kaua.ecommerce.auth.infrastructure.userdetails;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.infrastructure.IntegrationTest;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

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
        Assertions.assertTrue(aUserDetails.isAccountNonExpired());
        Assertions.assertTrue(aUserDetails.isAccountNonLocked());
        Assertions.assertTrue(aUserDetails.isCredentialsNonExpired());
        Assertions.assertTrue(aUserDetails.isEnabled());
    }

    @Test
    void givenAnNonExistsUser_whenLoadUserByUsername_thenShouldThrowNotFoundException() {
        final var aUserDetailsService = new UserDetailsServiceImpl(
                userJpaEntityRepository,
                roleJpaEntityRepository
        );

        final var aUserEmail = Fixture.Users.email();

        final var expectedMessage = "Email not found";

        final var aException = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> aUserDetailsService.loadUserByUsername(aUserEmail)
        );

        Assertions.assertEquals(expectedMessage, aException.getMessage());
    }

    @Test
    void givenADeletedUser_whenLoadUserByUsername_thenShouldThrowException() {
        final var aRoleDefault = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleDefault));

        final var aUser = Fixture.Users.randomUser(aRoleDefault.getId());
        final var aUserEmail = aUser.getEmail().value();

        aUser.markAsDeleted();

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());
        Assertions.assertEquals(1, this.roleJpaEntityRepository.count());

        final var aUserDetailsService = new UserDetailsServiceImpl(
                userJpaEntityRepository,
                roleJpaEntityRepository
        );

        final var expectedMessage = "User is deleted";

        final var aException = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> aUserDetailsService.loadUserByUsername(aUserEmail)
        );

        Assertions.assertEquals(expectedMessage, aException.getMessage());
    }

    @Test
    void givenAnUserWithThreeRolesTwoDeletedAndOneDefault_whenLoadUserByUsername_thenShouldReturnUserDetailsWithOneRole() {
        final var aRoleDefault = Fixture.Roles.defaultRole();
        final var aRoleOne = Fixture.Roles.randomRole();
        final var aRoleTwo = Fixture.Roles.randomRole().markAsDeleted();
        final var aRoleThree = Fixture.Roles.randomRole().markAsDeleted();

        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleDefault));
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleOne));
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleTwo));
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aRoleThree));

        final var aUser = Fixture.Users.randomUser(aRoleDefault.getId());
        aUser.addRoles(Set.of(aRoleOne.getId(), aRoleTwo.getId(), aRoleThree.getId()));

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());
        Assertions.assertEquals(4, this.roleJpaEntityRepository.count());

        final var aUserDetailsService = new UserDetailsServiceImpl(
                userJpaEntityRepository,
                roleJpaEntityRepository
        );

        final var aUserDetails = Assertions.assertDoesNotThrow(() ->
                aUserDetailsService.loadUserByUsername(aUser.getEmail().value()));

        Assertions.assertEquals(aUser.getId().value().toString(), aUserDetails.getUsername());
        Assertions.assertEquals(aUser.getPassword().value(), aUserDetails.getPassword());
        Assertions.assertEquals(2, aUserDetails.getAuthorities().size());
        Assertions.assertTrue(aUserDetails.isAccountNonExpired());
        Assertions.assertTrue(aUserDetails.isAccountNonLocked());
        Assertions.assertTrue(aUserDetails.isCredentialsNonExpired());
        Assertions.assertTrue(aUserDetails.isEnabled());

        final var aUserAfterRemoveDeletedRoles = this.userJpaEntityRepository
                .findById(aUser.getId().value()).get().toDomain();

        Assertions.assertEquals(2, aUserAfterRemoveDeletedRoles.getRoles().size());
    }
}
