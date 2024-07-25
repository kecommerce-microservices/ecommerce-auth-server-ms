package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.auth.domain.exceptions.RoleIsDeletedException;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class RoleTest extends UnitTest {

    @Test
    void givenAValidValues_whenCreate_thenRoleIsCreated() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        Assertions.assertNotNull(aRole);
        Assertions.assertNotNull(aRole.getId());
        Assertions.assertEquals(0, aRole.getVersion());
        Assertions.assertEquals(aName, aRole.getName());
        Assertions.assertEquals(aDescription, aRole.getDescription());
        Assertions.assertEquals(aIsDefault, aRole.isDefault());
        Assertions.assertFalse(aRole.isDeleted());
        Assertions.assertNotNull(aRole.getCreatedAt());
        Assertions.assertNotNull(aRole.getUpdatedAt());
        Assertions.assertTrue(aRole.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidValuesWithNullRoleDescription_whenCreate_thenRoleIsCreated() {
        final var aName = new RoleName("Admin");
        final RoleDescription aDescription = null;
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        Assertions.assertNotNull(aRole);
        Assertions.assertNotNull(aRole.getId());
        Assertions.assertEquals(0, aRole.getVersion());
        Assertions.assertEquals(aName, aRole.getName());
        Assertions.assertEquals("", aRole.getDescription().value());
        Assertions.assertEquals(aIsDefault, aRole.isDefault());
        Assertions.assertFalse(aRole.isDeleted());
        Assertions.assertNotNull(aRole.getCreatedAt());
        Assertions.assertNotNull(aRole.getUpdatedAt());
        Assertions.assertTrue(aRole.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidValues_whenWith_thenRoleIsCreated() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewUUID());
        final var aVersion = 0L;
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;
        final var aIsDeleted = false;
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();
        final Instant aDeletedAt = null;

        final var aRole = Role.with(aRoleId, aVersion, aName, aDescription, aIsDefault, aIsDeleted, aCreatedAt, aUpdatedAt, aDeletedAt);

        Assertions.assertNotNull(aRole);
        Assertions.assertEquals(aRoleId, aRole.getId());
        Assertions.assertEquals(aName, aRole.getName());
        Assertions.assertEquals(aDescription, aRole.getDescription());
        Assertions.assertEquals(aIsDefault, aRole.isDefault());
        Assertions.assertEquals(aIsDeleted, aRole.isDeleted());
        Assertions.assertEquals(aCreatedAt, aRole.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aRole.getUpdatedAt());
        Assertions.assertEquals(aDeletedAt, aRole.getDeletedAt().orElse(null));
    }

    @Test
    void givenAValidRole_whenCallToString_thenGetRoleAsString() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        final var aRoleAsString = aRole.toString();

        Assertions.assertNotNull(aRoleAsString);
        Assertions.assertTrue(aRoleAsString.contains("Role"));
        Assertions.assertTrue(aRoleAsString.contains("id="));
        Assertions.assertTrue(aRoleAsString.contains("version="));
        Assertions.assertTrue(aRoleAsString.contains("name="));
        Assertions.assertTrue(aRoleAsString.contains("description="));
        Assertions.assertTrue(aRoleAsString.contains("isDefault="));
        Assertions.assertTrue(aRoleAsString.contains("isDeleted="));
        Assertions.assertTrue(aRoleAsString.contains("createdAt="));
        Assertions.assertTrue(aRoleAsString.contains("updatedAt="));
        Assertions.assertTrue(aRoleAsString.contains("deletedAt="));
    }

    @Test
    void givenAnInvalidNullRoleName_whenCallCreate_thenThrowsDomainException() {
        final RoleName aName = null;
        final var aDescription = new RoleDescription("");
        final var aIsDefault = false;

        final var expectedPropertyName = "name";
        final var expectedMessage = "should not be null";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Role.create(aName, aDescription, aIsDefault));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallWith_thenThrowsDomainException() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewUUID());
        final var aVersion = 0L;
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;
        final var aIsDeleted = false;
        final Instant aCreatedAt = null;
        final var aUpdatedAt = InstantUtils.now();
        final Instant aDeletedAt = null;

        final var expectedPropertyName = "createdAt";
        final var expectedMessage = "should not be null";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Role.with(aRoleId, aVersion, aName, aDescription, aIsDefault, aIsDeleted, aCreatedAt, aUpdatedAt, aDeletedAt));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallWith_thenThrowsDomainException() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewUUID());
        final var aVersion = 0L;
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;
        final var aIsDeleted = false;
        final var aCreatedAt = InstantUtils.now();
        final Instant aUpdatedAt = null;
        final Instant aDeletedAt = null;

        final var expectedPropertyName = "updatedAt";
        final var expectedMessage = "should not be null";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Role.with(aRoleId, aVersion, aName, aDescription, aIsDefault, aIsDeleted, aCreatedAt, aUpdatedAt, aDeletedAt));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAValidValues_whenUpdate_thenRoleIsUpdated() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);
        final var aRoleUpdatedAt = aRole.getUpdatedAt();

        final var aNewName = new RoleName("Super Admin");
        final var aNewDescription = new RoleDescription("Super Administrator");
        final var aNewIsDefault = true;

        final var aUpdatedRole = aRole.update(aNewName, aNewDescription, aNewIsDefault);

        Assertions.assertNotNull(aUpdatedRole);
        Assertions.assertEquals(aRole.getId(), aUpdatedRole.getId());
        Assertions.assertEquals(0, aUpdatedRole.getVersion());
        Assertions.assertEquals(aNewName, aUpdatedRole.getName());
        Assertions.assertEquals(aNewDescription, aUpdatedRole.getDescription());
        Assertions.assertEquals(aNewIsDefault, aUpdatedRole.isDefault());
        Assertions.assertFalse(aUpdatedRole.isDeleted());
        Assertions.assertEquals(aRole.getCreatedAt(), aUpdatedRole.getCreatedAt());
        Assertions.assertTrue(aRoleUpdatedAt.isBefore(aUpdatedRole.getUpdatedAt()));
        Assertions.assertTrue(aUpdatedRole.getDeletedAt().isEmpty());
    }

    @Test
    void givenADeletedRole_whenUpdate_thenThrowsRoleIsDeletedException() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        final var aNewName = new RoleName("Super Admin");
        final var aNewDescription = new RoleDescription("Super Administrator");
        final var aNewIsDefault = true;

        aRole.markAsDeleted();

        final var expectedMessage = "Role with id " + aRole.getId().value() + " is deleted";

        final var aException = Assertions.assertThrows(RoleIsDeletedException.class,
                () -> aRole.update(aNewName, aNewDescription, aNewIsDefault));

        Assertions.assertEquals(expectedMessage, aException.getMessage());
    }

    @Test
    void givenAValidDeletedRole_whenMarkAsDeleted_thenRoleIsDeleted() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);
        final var aRoleUpdatedAt = aRole.getUpdatedAt();

        final var aDeletedRole = aRole.markAsDeleted();

        Assertions.assertNotNull(aDeletedRole);
        Assertions.assertEquals(aRole.getId(), aDeletedRole.getId());
        Assertions.assertEquals(0, aDeletedRole.getVersion());
        Assertions.assertEquals(aRole.getName(), aDeletedRole.getName());
        Assertions.assertEquals(aRole.getDescription(), aDeletedRole.getDescription());
        Assertions.assertEquals(aRole.isDefault(), aDeletedRole.isDefault());
        Assertions.assertTrue(aDeletedRole.isDeleted());
        Assertions.assertEquals(aRole.getCreatedAt(), aDeletedRole.getCreatedAt());
        Assertions.assertTrue(aRoleUpdatedAt.isBefore(aDeletedRole.getUpdatedAt()));
        Assertions.assertNotNull(aDeletedRole.getDeletedAt());
    }

    @Test
    void givenADeletedRole_whenMarkAsDeleted_thenThrowsRoleIsDeletedException() {
        final var aName = new RoleName("Admin");
        final var aDescription = new RoleDescription("Administrator");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        aRole.markAsDeleted();

        final var expectedMessage = "Role with id " + aRole.getId().value() + " is deleted";

        final var aException = Assertions.assertThrows(RoleIsDeletedException.class,
                aRole::markAsDeleted);

        Assertions.assertEquals(expectedMessage, aException.getMessage());
    }

    @Test
    void givenAValidUUIDString_whenCreateRoleId_thenRoleIdIsCreated() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewUUID().toString());

        Assertions.assertNotNull(aRoleId);
        Assertions.assertEquals(36, aRoleId.value().toString().length());
    }
}
