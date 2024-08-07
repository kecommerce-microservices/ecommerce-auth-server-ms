package com.kaua.ecommerce.auth.infrastructure.mailtokens;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@DatabaseRepositoryTest
class MailTokenRepositoryImplTest {

    @Autowired
    private MailRepositoryImpl mailRepositoryImpl;

    @Autowired
    private MailTokenJpaEntityRepository mailTokenJpaEntityRepository;

    @Test
    void givenAValidValues_whenCallSave_thenSaveMailToken() {
        final var aMail = Fixture.Mails.mail(
                "testes.tess@gmail.com",
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );

        Assertions.assertEquals(0, mailTokenJpaEntityRepository.count());

        final var aOutput = this.mailRepositoryImpl.save(aMail);

        Assertions.assertEquals(1, mailTokenJpaEntityRepository.count());

        Assertions.assertEquals(aMail.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aMail.getEmail(), aOutput.getEmail());
        Assertions.assertEquals(aMail.getUserId().value(), aOutput.getUserId().value());
        Assertions.assertEquals(aMail.getToken(), aOutput.getToken());
        Assertions.assertEquals(aMail.getType(), aOutput.getType());
        Assertions.assertEquals(aMail.isUsed(), aOutput.isUsed());
        Assertions.assertEquals(aMail.getUsedAt(), aOutput.getUsedAt());
        Assertions.assertEquals(aMail.getExpiresAt(), aOutput.getExpiresAt());
        Assertions.assertEquals(aMail.getCreatedAt(), aOutput.getCreatedAt());
    }

    @Test
    void givenAnPrePersistedTokens_whenCallFindByEmail_thenReturnMailsTokens() {
        final var aEmail = Fixture.Users.email();
        final var aUserId = IdentifierUtils.generateNewId();
        final var aMailOne = Fixture.Mails.mail(
                aEmail,
                aUserId,
                MailType.EMAIL_CONFIRMATION
        );

        final var aMailTwo = Fixture.Mails.mail(
                aEmail,
                aUserId,
                MailType.PASSWORD_RESET
        );

        mailTokenJpaEntityRepository.saveAllAndFlush(Set.of(
                MailTokenJpaEntity.toEntity(aMailOne),
                MailTokenJpaEntity.toEntity(aMailTwo)
        ));

        Assertions.assertEquals(2, mailTokenJpaEntityRepository.count());

        final var aOutput = this.mailRepositoryImpl.findByEmail(aEmail);

        Assertions.assertEquals(2, aOutput.size());
    }

    @Test
    void givenAnPrePersistedMail_whenCallDeleteByToken_thenDeleteMailToken() {
        final var aEmail = Fixture.Users.email();
        final var aUserId = IdentifierUtils.generateNewId();
        final var aMail = Fixture.Mails.mail(
                aEmail,
                aUserId,
                MailType.EMAIL_CONFIRMATION
        );

        mailTokenJpaEntityRepository.saveAndFlush(MailTokenJpaEntity.toEntity(aMail));

        Assertions.assertEquals(1, mailTokenJpaEntityRepository.count());

        this.mailRepositoryImpl.deleteByToken(aMail.getToken());

        Assertions.assertEquals(0, mailTokenJpaEntityRepository.count());
    }
}
