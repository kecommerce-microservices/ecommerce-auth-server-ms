package com.kaua.ecommerce.auth.infrastructure.roles.persistence;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@DatabaseRepositoryTest
class RoleJpaEntityRepositoryTest {

    @Autowired
    private RoleJpaEntityRepository roleJpaEntityRepository;

    @Test
    void givenAnInvalidNullName_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity.name";

        final var aRole = Fixture.Roles.randomRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setName(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> roleJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullDescription_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "description";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity.description";

        final var aRole = Fixture.Roles.randomRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setDescription(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> roleJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity.createdAt";

        final var aRole = Fixture.Roles.randomRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> roleJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity.updatedAt";

        final var aRole = Fixture.Roles.randomRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setUpdatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> roleJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity' must be manually assigned before calling 'persist()'";

        final var aRole = Fixture.Roles.randomRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> roleJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidFalseIsDefault_whenCallSave_shouldReturnAnEntity() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setDefault(false);

        final var aEntitySaved = roleJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getName(), aEntitySaved.getName());
        Assertions.assertEquals(aEntity.getDescription(), aEntitySaved.getDescription());
        Assertions.assertEquals(aEntity.isDefault(), aEntitySaved.isDefault());
        Assertions.assertEquals(aEntity.isDeleted(), aEntitySaved.isDeleted());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), aEntitySaved.getUpdatedAt());
        Assertions.assertEquals(aEntity.getDeletedAt(), aEntitySaved.getDeletedAt());
        Assertions.assertEquals(aEntity.getVersion(), aEntitySaved.getVersion());
    }

    @Test
    void givenAValidTrueIsDeleted_whenCallSave_shouldReturnAnEntity() {
        final var aRole = Fixture.Roles.defaultRole();
        final var aEntity = RoleJpaEntity.toEntity(aRole);
        aEntity.setDeleted(true);
        aEntity.setDeletedAt(InstantUtils.now());

        final var aEntitySaved = roleJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getName(), aEntitySaved.getName());
        Assertions.assertEquals(aEntity.getDescription(), aEntitySaved.getDescription());
        Assertions.assertEquals(aEntity.isDefault(), aEntitySaved.isDefault());
        Assertions.assertEquals(aEntity.isDeleted(), aEntitySaved.isDeleted());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
        Assertions.assertEquals(aEntity.getUpdatedAt(), aEntitySaved.getUpdatedAt());
        Assertions.assertEquals(aEntity.getDeletedAt(), aEntitySaved.getDeletedAt());
        Assertions.assertEquals(aEntity.getVersion(), aEntitySaved.getVersion());
    }
}
