package com.kaua.ecommerce.auth.domain.mailtokens;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.auth.domain.users.UserId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

class MailTokenTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewMailToken_thenShouldReturnAMailToken() {
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aType = MailType.EMAIL_CONFIRMATION;
        final var aExpiresAt = InstantUtils.now().plus(10, ChronoUnit.HOURS);

        final var aMailToken = MailToken.newMailToken(
                aMail,
                aUserId,
                aToken,
                aType,
                aExpiresAt
        );

        Assertions.assertNotNull(aMailToken);
        Assertions.assertNotNull(aMailToken.getId());
        Assertions.assertEquals(aMail, aMailToken.getEmail());
        Assertions.assertEquals(aUserId, aMailToken.getUserId());
        Assertions.assertEquals(aToken, aMailToken.getToken());
        Assertions.assertEquals(aType, aMailToken.getType());
        Assertions.assertFalse(aMailToken.isUsed());
        Assertions.assertTrue(aMailToken.getUsedAt().isEmpty());
        Assertions.assertEquals(aExpiresAt, aMailToken.getExpiresAt());
        Assertions.assertNotNull(aMailToken.getCreatedAt());
    }

    @Test
    void givenAValidValues_whenCallWith_thenShouldReturnAMailToken() {
        final var aMailId = new MailId(IdentifierUtils.generateNewId());
        final var aVersion = 0;
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aIsUsed = false;
        final var aType = MailType.EMAIL_CONFIRMATION;
        final Instant aUsedAt = null;
        final var aExpiresAt = InstantUtils.now().plus(10, ChronoUnit.HOURS);
        final var aCreatedAt = InstantUtils.now();

        final var aMailToken = MailToken.with(
                aMailId,
                aVersion,
                aMail,
                aUserId,
                aToken,
                aIsUsed,
                aType,
                aUsedAt,
                aExpiresAt,
                aCreatedAt
        );

        Assertions.assertNotNull(aMailToken);
        Assertions.assertEquals(aMailId, aMailToken.getId());
        Assertions.assertEquals(aVersion, aMailToken.getVersion());
        Assertions.assertEquals(aMail, aMailToken.getEmail());
        Assertions.assertEquals(aUserId, aMailToken.getUserId());
        Assertions.assertEquals(aToken, aMailToken.getToken());
        Assertions.assertEquals(aIsUsed, aMailToken.isUsed());
        Assertions.assertEquals(aType, aMailToken.getType());
        Assertions.assertTrue(aMailToken.getUsedAt().isEmpty());
        Assertions.assertEquals(aExpiresAt, aMailToken.getExpiresAt());
        Assertions.assertEquals(aCreatedAt, aMailToken.getCreatedAt());
    }

    @Test
    void testCallMailTokenToString() {
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aType = MailType.EMAIL_CONFIRMATION;
        final var aExpiresAt = InstantUtils.now().plus(10, ChronoUnit.HOURS);

        final var aMailToken = MailToken.newMailToken(
                aMail,
                aUserId,
                aToken,
                aType,
                aExpiresAt
        );

        final var aMailTokenToString = aMailToken.toString();

        Assertions.assertNotNull(aMailTokenToString);
        Assertions.assertTrue(aMailTokenToString.contains("MailToken"));
        Assertions.assertTrue(aMailTokenToString.contains("email=" + aMail));
        Assertions.assertTrue(aMailTokenToString.contains("userId=" + aUserId.value()));
        Assertions.assertTrue(aMailTokenToString.contains("type=" + aType.name()));
        Assertions.assertTrue(aMailTokenToString.contains("isUsed=false"));
        Assertions.assertTrue(aMailTokenToString.contains("usedAt=null"));
        Assertions.assertTrue(aMailTokenToString.contains("expiresAt=" + aExpiresAt));
        Assertions.assertTrue(aMailTokenToString.contains("createdAt=" + aMailToken.getCreatedAt()));
    }

    @Test
    void givenAValidValue_whenCallMailTypeOf_thenShouldReturnMailType() {
        final var aMailType = MailType.EMAIL_CONFIRMATION;
        final var aMailTypeOf = MailType.of(aMailType.name());

        Assertions.assertTrue(aMailTypeOf.isPresent());
        Assertions.assertEquals(aMailType, aMailTypeOf.get());
    }

    @Test
    void givenAValidMailTokenButExpired_whenCallIsExpired_thenShouldReturnTrue() {
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aType = MailType.EMAIL_CONFIRMATION;
        final var aExpiresAt = InstantUtils.now().minus(10, ChronoUnit.HOURS);

        final var aMailToken = MailToken.newMailToken(
                aMail,
                aUserId,
                aToken,
                aType,
                aExpiresAt
        );

        final var aIsExpired = aMailToken.isExpired();

        Assertions.assertTrue(aIsExpired);
    }

    @Test
    void givenAValidMailTokenButNotExpired_whenCallIsExpired_thenShouldReturnFalse() {
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aType = MailType.EMAIL_CONFIRMATION;
        final var aExpiresAt = InstantUtils.now().plus(10, ChronoUnit.HOURS);

        final var aMailToken = MailToken.newMailToken(
                aMail,
                aUserId,
                aToken,
                aType,
                aExpiresAt
        );

        final var aIsExpired = aMailToken.isExpired();

        Assertions.assertFalse(aIsExpired);
    }

    @Test
    void givenAValidMailToken_whenCallMarkAsUsed_thenShouldReturnTrue() {
        final var aMail = Fixture.Users.email();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aToken = IdentifierUtils.generateNewIdWithoutHyphen();
        final var aType = MailType.EMAIL_CONFIRMATION;
        final var aExpiresAt = InstantUtils.now().plus(10, ChronoUnit.HOURS);

        final var aMailToken = MailToken.newMailToken(
                aMail,
                aUserId,
                aToken,
                aType,
                aExpiresAt
        );

        aMailToken.markAsUsed();

        Assertions.assertTrue(aMailToken.isUsed());
        Assertions.assertTrue(aMailToken.getUsedAt().isPresent());
    }
}
