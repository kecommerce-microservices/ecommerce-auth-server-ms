package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.Set;

@DatabaseRepositoryTest
class UserJpaEntityRepositoryTest {

    @Autowired
    private UserJpaEntityRepository userJpaEntityRepository;

    @Autowired
    private RoleJpaEntityRepository roleJpaEntityRepository;

    @Test
    void givenAnInvalidNullCustomerId_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "customerId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.customerId";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setCustomerId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullFirstName_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "firstName";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.firstName";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setFirstName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullLastName_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "lastName";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.lastName";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setLastName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullEmail_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "email";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.email";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setEmail(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullPassword_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "password";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.password";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setPassword(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.createdAt";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity.updatedAt";

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity' must be manually assigned before calling 'persist()'";

        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidSetRoles_whenCallSave_shouldReturnAnEntity() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setRoles(Set.of(new UserRoleJpaEntity(aEntity, aRole.getId())));

        final var aEntitySaved = userJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getCustomerId(), aEntitySaved.getCustomerId());
        Assertions.assertEquals(aEntity.getFirstName(), aEntitySaved.getFirstName());
        Assertions.assertEquals(aEntity.getLastName(), aEntitySaved.getLastName());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getPassword(), aEntitySaved.getPassword());
        Assertions.assertEquals(aEntity.isDeleted(), aEntitySaved.isDeleted());
        Assertions.assertEquals(aEntity.isEmailVerified(), aEntitySaved.isEmailVerified());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), aEntitySaved.getUpdatedAt());
        Assertions.assertEquals(aEntity.getDeletedAt(), aEntitySaved.getDeletedAt());
        Assertions.assertEquals(aEntity.getVersion(), aEntitySaved.getVersion());
    }

    @Test
    void givenAValidSetTrueIsDeleted_whenCallSave_shouldReturnAnEntity() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setDeleted(true);
        aEntity.setDeletedAt(InstantUtils.now());

        final var aEntitySaved = userJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getCustomerId(), aEntitySaved.getCustomerId());
        Assertions.assertEquals(aEntity.getFirstName(), aEntitySaved.getFirstName());
        Assertions.assertEquals(aEntity.getLastName(), aEntitySaved.getLastName());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getPassword(), aEntitySaved.getPassword());
        Assertions.assertEquals(aEntity.isDeleted(), aEntitySaved.isDeleted());
        Assertions.assertEquals(aEntity.isEmailVerified(), aEntitySaved.isEmailVerified());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), aEntitySaved.getUpdatedAt());
        Assertions.assertEquals(aEntity.getDeletedAt(), aEntitySaved.getDeletedAt());
        Assertions.assertEquals(aEntity.getVersion(), aEntitySaved.getVersion());
    }

    @Test
    void givenAValidSetTrueEmailVerified_whenCallSave_shouldReturnAnEntity() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        aEntity.setEmailVerified(true);

        final var aEntitySaved = userJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getCustomerId(), aEntitySaved.getCustomerId());
        Assertions.assertEquals(aEntity.getFirstName(), aEntitySaved.getFirstName());
        Assertions.assertEquals(aEntity.getLastName(), aEntitySaved.getLastName());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getPassword(), aEntitySaved.getPassword());
        Assertions.assertEquals(aEntity.isDeleted(), aEntitySaved.isDeleted());
        Assertions.assertEquals(aEntity.isEmailVerified(), aEntitySaved.isEmailVerified());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), aEntitySaved.getUpdatedAt());
        Assertions.assertEquals(aEntity.getDeletedAt(), aEntitySaved.getDeletedAt());
        Assertions.assertEquals(aEntity.getVersion(), aEntitySaved.getVersion());
    }

    @Test
    void givenAValidSetValuesInUserMfaJpaEntity_whenCallSave_thenReturnUserWithMfaSaved() {
        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        final var aMfa = UserMfaJpaEntity.toEntity(aUser.getMfa());
        aMfa.setMfaEnabled(true);
        aMfa.setMfaVerified(true);
        aMfa.setMfaSecret("mfaSecret");
        aMfa.setDeviceName("deviceName");
        aMfa.setDeviceVerified(true);
        aMfa.setMfaType(UserMfaType.TOTP);
        aMfa.setCreatedAt(InstantUtils.now());
        aMfa.setUpdatedAt(InstantUtils.now());
        aMfa.setValidUntil(InstantUtils.now());
        aEntity.setMfa(aMfa);

        final var aEntitySaved = userJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertTrue(aEntity.getMfa().isMfaEnabled());
        Assertions.assertTrue(aEntity.getMfa().isMfaVerified());
        Assertions.assertEquals(aEntity.getMfa().getMfaSecret(), aEntitySaved.getMfa().getMfaSecret());
        Assertions.assertEquals(aEntity.getMfa().getDeviceName(), aEntitySaved.getMfa().getDeviceName());
        Assertions.assertTrue(aEntity.getMfa().isDeviceVerified());
        Assertions.assertEquals(aEntity.getMfa().getMfaType(), aEntitySaved.getMfa().getMfaType());
        Assertions.assertEquals(aEntity.getMfa().getCreatedAt(), aEntitySaved.getMfa().getCreatedAt());
        Assertions.assertEquals(aEntity.getMfa().getUpdatedAt(), aEntitySaved.getMfa().getUpdatedAt());
        Assertions.assertEquals(aEntity.getMfa().getValidUntil(), aEntitySaved.getMfa().getValidUntil());
    }

    @Test
    void givenAnInvalidNullMfaId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.ecommerce.auth.infrastructure.users.persistence.UserMfaJpaEntity' must be manually assigned before calling 'persist()'";

        final var aRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(aRole));

        final var aUser = Fixture.Users.randomUser(aRole.getId());
        final var aEntity = UserJpaEntity.toEntity(aUser);
        final var aMfa = UserMfaJpaEntity.toEntity(aUser.getMfa());
        aMfa.setId(null);
        aEntity.setMfa(aMfa);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> userJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
