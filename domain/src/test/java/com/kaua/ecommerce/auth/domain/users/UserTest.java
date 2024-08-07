package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfa;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class UserTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewUser_thenShouldReturnUser() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("teste@teste.com");
        final var aPassword = new UserPassword("123456Am@");
        final var aRoles = Set.of(new RoleId(IdentifierUtils.generateNewId()));

        final var aUser = User.newUser(aCustomerId, aName, aEmail, aPassword, aRoles);

        Assertions.assertNotNull(aUser);
        Assertions.assertEquals(aCustomerId, aUser.getCustomerId());
        Assertions.assertEquals(aName, aUser.getName());
        Assertions.assertEquals(aEmail, aUser.getEmail());
        Assertions.assertEquals(aPassword, aUser.getPassword());
        Assertions.assertEquals(aRoles, aUser.getRoles());
        Assertions.assertFalse(aUser.isDeleted());
        Assertions.assertFalse(aUser.isEmailVerified());
        Assertions.assertNotNull(aUser.getCreatedAt());
        Assertions.assertNotNull(aUser.getUpdatedAt());
        Assertions.assertTrue(aUser.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidValues_whenCallWith_thenShouldReturnUser() {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aVersion = 1L;
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("example@teste.com");
        final var aPassword = new UserPassword("123456Am@");
        final var aRoles = Set.of(new RoleId(IdentifierUtils.generateNewId()));
        final var aIsDeleted = false;
        final var aEmailVerified = false;
        final var aUserMfa = UserMfa.newMfa();
        final var aNow = InstantUtils.now();

        final var aUser = User.with(
                aUserId,
                aVersion,
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles,
                aIsDeleted,
                aEmailVerified,
                aUserMfa,
                aNow,
                aNow,
                null
        );

        Assertions.assertNotNull(aUser);
        Assertions.assertEquals(aUserId, aUser.getId());
        Assertions.assertEquals(aVersion, aUser.getVersion());
        Assertions.assertEquals(aCustomerId, aUser.getCustomerId());
        Assertions.assertEquals(aName, aUser.getName());
        Assertions.assertEquals(aEmail, aUser.getEmail());
        Assertions.assertEquals(aPassword, aUser.getPassword());
        Assertions.assertEquals(aRoles, aUser.getRoles());
        Assertions.assertEquals(aIsDeleted, aUser.isDeleted());
        Assertions.assertEquals(aEmailVerified, aUser.isEmailVerified());
        Assertions.assertEquals(aNow, aUser.getCreatedAt());
        Assertions.assertEquals(aNow, aUser.getUpdatedAt());
        Assertions.assertTrue(aUser.getDeletedAt().isEmpty());
    }

    @Test
    void testCallUserToString() {
        final var aUserId = new UserId(IdentifierUtils.generateNewId());
        final var aVersion = 1L;
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("testes@test.com");
        final var aPassword = new UserPassword("123456Am@");
        final var aRoles = Set.of(new RoleId(IdentifierUtils.generateNewId()));
        final var aIsDeleted = false;
        final var aEmailVerified = false;
        final var aUserMfa = UserMfa.newMfa();
        final var aNow = InstantUtils.now();

        final var aUser = User.with(
                aUserId,
                aVersion,
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles,
                aIsDeleted,
                aEmailVerified,
                aUserMfa,
                aNow,
                aNow,
                null
        );

        final var aUserToString = aUser.toString();

        Assertions.assertNotNull(aUserToString);
        Assertions.assertTrue(aUserToString.contains("id=" + aUserId.value()));
        Assertions.assertTrue(aUserToString.contains("version=" + aVersion));
        Assertions.assertTrue(aUserToString.contains("customerId=" + aCustomerId.value()));
        Assertions.assertTrue(aUserToString.contains("name=" + aName.fullName()));
        Assertions.assertTrue(aUserToString.contains("email=" + aEmail.value()));
        Assertions.assertTrue(aUserToString.contains("roles=" + aRoles.size()));
        Assertions.assertTrue(aUserToString.contains("isDeleted=" + aIsDeleted));
        Assertions.assertTrue(aUserToString.contains("emailVerified=" + aEmailVerified));
        Assertions.assertTrue(aUserToString.contains("mfa=" + aUserMfa));
        Assertions.assertTrue(aUserToString.contains("createdAt=" + aNow));
        Assertions.assertTrue(aUserToString.contains("updatedAt=" + aNow));
        Assertions.assertTrue(aUserToString.contains("deletedAt=null"));
    }

    @Test
    void givenAnInvalidEmptyRoles_whenCallNewUser_thenThrowsDomainException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("teste.teste@test.com");
        final var aPassword = new UserPassword("123456Am@");
        final var aRoles = Set.<RoleId>of();

        final var aProperty = "roles";
        final var aMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> User.newUser(aCustomerId, aName, aEmail, aPassword, aRoles));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullRoles_whenCallNewUser_thenThrowsDomainException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("teste.teste@test.com");
        final var aPassword = new UserPassword("123456Am@");
        final Set<RoleId> aRoles = null;

        final var aProperty = "roles";
        final var aMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> User.newUser(aCustomerId, aName, aEmail, aPassword, aRoles));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAValidName_whenCallChangeName_thenShouldChangeName() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        final var aName = new UserName("Fulano", "Doe");

        final var aUserChanged = aUser.changeName(aName);

        Assertions.assertNotNull(aUserChanged);
        Assertions.assertEquals(aName, aUserChanged.getName());
        Assertions.assertTrue(aUserChanged.getUpdatedAt().isAfter(aUpdatedAt));
    }

    @Test
    void givenAValidEmail_whenCallChangeEmail_thenShouldChangeEmail() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        final var aEmail = new UserEmail("fulaninho@teste.com");

        final var aUserChanged = aUser.changeEmail(aEmail);

        Assertions.assertNotNull(aUserChanged);
        Assertions.assertEquals(aEmail, aUserChanged.getEmail());
        Assertions.assertTrue(aUserChanged.getUpdatedAt().isAfter(aUpdatedAt));
    }

    @Test
    void givenAValidUser_whenCallMarkAsDeleted_thenShouldMarkAsDeleted() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aDeletedAt = aUser.getDeletedAt();

        final var aUserDeleted = aUser.markAsDeleted();

        Assertions.assertNotNull(aUserDeleted);
        Assertions.assertTrue(aUserDeleted.isDeleted());
        Assertions.assertTrue(aUserDeleted.getDeletedAt().isPresent());
        Assertions.assertTrue(aUserDeleted.getDeletedAt().get().isAfter(aDeletedAt.orElse(InstantUtils.now().minusSeconds(1))));
    }

    @Test
    void givenAValidRoles_whenCallAddRoles_thenShouldAddRoles() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aRoles = Set.of(new RoleId(IdentifierUtils.generateNewId()));

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.addRoles(aRoles);

        Assertions.assertTrue(aUser.getRoles().containsAll(aRoles));
        Assertions.assertTrue(aUser.getUpdatedAt().isAfter(aUpdatedAt));
    }

    @Test
    void givenAValidRoleId_whenCallRemoveRole_thenShouldRemoveRole() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aRandomRole = Fixture.Roles.randomRole();

        final var aRoleId = aRandomRole.getId();

        aUser.addRoles(Set.of(aRoleId));

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.removeRole(aRoleId);

        Assertions.assertFalse(aUser.getRoles().contains(aRoleId));
        Assertions.assertTrue(aUser.getUpdatedAt().isAfter(aUpdatedAt));
    }

    @Test
    void givenAnOneRole_whenCallRemoveRole_thenShouldThrowDomainException() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aMessage = "User must have at least one role";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aUser.removeRole(aRole));

        Assertions.assertEquals(aMessage, aException.getMessage());
    }

    @Test
    void givenAnNullRoleId_whenCallRemoveRole_thenShouldDoNothing() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.removeRole(null);

        Assertions.assertEquals(aUser.getUpdatedAt(), aUpdatedAt);
    }

    @Test
    void givenAnEmptyRoles_whenCallAddRoles_thenShouldDoNothing() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.addRoles(Set.of());

        Assertions.assertEquals(aUser.getUpdatedAt(), aUpdatedAt);
    }

    @Test
    void givenAnNullRoles_whenCallAddRoles_thenShouldDoNothing() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.addRoles(null);

        Assertions.assertEquals(aUser.getUpdatedAt(), aUpdatedAt);
    }

    @Test
    void givenAValidUser_whenCallConfirmEmail_thenShouldMarkAsEmailConfirmed() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        aUser.confirmEmail();

        Assertions.assertTrue(aUser.isEmailVerified());
        Assertions.assertTrue(aUser.getUpdatedAt().isAfter(aUpdatedAt));
    }

    @Test
    void givenAValidUser_whenCallChangePassword_thenShouldChangePassword() {
        final var aRole = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRole);

        final var aUpdatedAt = aUser.getUpdatedAt();

        final var aNewPassword = new UserPassword("123456Am@");

        aUser.changePassword(aNewPassword);

        Assertions.assertEquals(aNewPassword, aUser.getPassword());
        Assertions.assertTrue(aUser.getUpdatedAt().isAfter(aUpdatedAt));
    }
}
