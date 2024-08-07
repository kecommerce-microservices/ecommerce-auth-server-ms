package com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@DatabaseRepositoryTest
class MailTokenJpaEntityRepositoryTest {

    @Autowired
    private MailTokenJpaEntityRepository mailTokenJpaEntityRepository;

    @Test
    void givenAValidEntity_whenCallSave_shouldReturnAnEntity() {
        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);

        final var aEntitySaved = mailTokenJpaEntityRepository.save(aEntity);

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getUserId(), aEntitySaved.getUserId());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getToken(), aEntitySaved.getToken());
        Assertions.assertEquals(aEntity.isUsed(), aEntitySaved.isUsed());
        Assertions.assertEquals(aEntity.getType(), aEntitySaved.getType());
        Assertions.assertEquals(aEntity.getUsedAt(), aEntitySaved.getUsedAt());
        Assertions.assertEquals(aEntity.getExpiresAt(), aEntitySaved.getExpiresAt());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
    }

    @Test
    void givenAnInvalidNullEmail_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "email";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.email";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setEmail(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUserId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "userId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.userId";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setUserId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullToken_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "token";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.token";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setToken(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnFalseIsUsed_whenCallSave_shouldReturnAnEntity() {
        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setUsed(false);

        final var aEntitySaved = Assertions.assertDoesNotThrow(
                () -> mailTokenJpaEntityRepository.save(aEntity));

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getUserId(), aEntitySaved.getUserId());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getToken(), aEntitySaved.getToken());
        Assertions.assertEquals(aEntity.isUsed(), aEntitySaved.isUsed());
        Assertions.assertEquals(aEntity.getType(), aEntitySaved.getType());
        Assertions.assertEquals(aEntity.getUsedAt(), aEntitySaved.getUsedAt());
        Assertions.assertEquals(aEntity.getExpiresAt(), aEntitySaved.getExpiresAt());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
    }

    @Test
    void givenAnInvalidNullType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "type";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.type";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullExpiresAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "expiresAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.expiresAt";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setExpiresAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity.createdAt";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setCreatedAt(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity' must be manually assigned before calling 'persist()'";

        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> mailTokenJpaEntityRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidNullUsedAt_whenCallSave_shouldReturnAnEntity() {
        final var aMailToken = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );
        final var aEntity = MailTokenJpaEntity.toEntity(aMailToken);
        aEntity.setUsedAt(null);

        final var aEntitySaved = Assertions.assertDoesNotThrow(
                () -> mailTokenJpaEntityRepository.save(aEntity));

        Assertions.assertNotNull(aEntitySaved);
        Assertions.assertEquals(aEntity.getId(), aEntitySaved.getId());
        Assertions.assertEquals(aEntity.getUserId(), aEntitySaved.getUserId());
        Assertions.assertEquals(aEntity.getEmail(), aEntitySaved.getEmail());
        Assertions.assertEquals(aEntity.getToken(), aEntitySaved.getToken());
        Assertions.assertEquals(aEntity.isUsed(), aEntitySaved.isUsed());
        Assertions.assertEquals(aEntity.getType(), aEntitySaved.getType());
        Assertions.assertEquals(aEntity.getUsedAt(), aEntitySaved.getUsedAt());
        Assertions.assertEquals(aEntity.getExpiresAt(), aEntitySaved.getExpiresAt());
        Assertions.assertEquals(aEntity.getCreatedAt(), aEntitySaved.getCreatedAt());
    }
}
