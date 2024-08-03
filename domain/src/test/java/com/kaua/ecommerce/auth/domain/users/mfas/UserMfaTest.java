package com.kaua.ecommerce.auth.domain.users.mfas;

import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

public class UserMfaTest {

    @Test
    void testCallNewMfa_whenCalled_thenReturnNewUserMfa() {
        final var userMfa = UserMfa.newMfa();

        Assertions.assertNotNull(userMfa);
        Assertions.assertNotNull(userMfa.getId());
        Assertions.assertEquals(0, userMfa.getVersion());
        Assertions.assertFalse(userMfa.isMfaEnabled());
        Assertions.assertFalse(userMfa.isMfaVerified());
        Assertions.assertTrue(userMfa.getMfaSecret().isEmpty());
        Assertions.assertTrue(userMfa.getDeviceName().isEmpty());
        Assertions.assertFalse(userMfa.isDeviceVerified());
        Assertions.assertTrue(userMfa.getMfaType().isEmpty());
        Assertions.assertNotNull(userMfa.getCreatedAt());
        Assertions.assertNotNull(userMfa.getUpdatedAt());
        Assertions.assertTrue(userMfa.getValidUntil().isEmpty());
    }

    @Test
    void givenAValidValues_whenCallWith_thenReturnUserMfa() {
        final var aId = new UserMfaId(IdentifierUtils.generateNewId());
        final var aVersion = 0L;
        final var aMfaEnabled = false;
        final var aMfaVerified = false;
        final var aMfaSecret = "mfaSecret";
        final var aDeviceName = "deviceName";
        final var aDeviceVerified = false;
        final var aMfaType = UserMfaType.TOTP;
        final var aNow = InstantUtils.now();

        final var userMfa = UserMfa.with(
                aId,
                aVersion,
                aMfaEnabled,
                aMfaVerified,
                aMfaSecret,
                aDeviceName,
                aDeviceVerified,
                aMfaType,
                aNow,
                aNow,
                aNow
        );

        Assertions.assertNotNull(userMfa);
        Assertions.assertEquals(aId, userMfa.getId());
        Assertions.assertEquals(aVersion, userMfa.getVersion());
        Assertions.assertEquals(aMfaEnabled, userMfa.isMfaEnabled());
        Assertions.assertEquals(aMfaVerified, userMfa.isMfaVerified());
        Assertions.assertEquals(aMfaSecret, userMfa.getMfaSecret().get());
        Assertions.assertEquals(aDeviceName, userMfa.getDeviceName().get());
        Assertions.assertEquals(aDeviceVerified, userMfa.isDeviceVerified());
        Assertions.assertEquals(aMfaType, userMfa.getMfaType().get());
        Assertions.assertEquals(aNow, userMfa.getCreatedAt());
        Assertions.assertEquals(aNow, userMfa.getUpdatedAt());
        Assertions.assertEquals(aNow, userMfa.getValidUntil().get());
    }

    @Test
    void testCallUserMfaToString() {
        final var userMfa = UserMfa.newMfa();

        final var userMfaString = userMfa.toString();

        Assertions.assertNotNull(userMfaString);
        Assertions.assertTrue(userMfaString.contains("UserMfa"));
        Assertions.assertTrue(userMfaString.contains("id="));
        Assertions.assertTrue(userMfaString.contains("version="));
        Assertions.assertTrue(userMfaString.contains("mfaEnabled="));
        Assertions.assertTrue(userMfaString.contains("mfaVerified="));
        Assertions.assertTrue(userMfaString.contains("deviceName="));
        Assertions.assertTrue(userMfaString.contains("deviceVerified="));
        Assertions.assertTrue(userMfaString.contains("mfaType="));
        Assertions.assertTrue(userMfaString.contains("createdAt="));
        Assertions.assertTrue(userMfaString.contains("updatedAt="));
        Assertions.assertTrue(userMfaString.contains("validUntil="));
    }

    @Test
    void givenAValidType_whenCallUserMfaTypeOf_thenReturnUserMfaType() {
        final var aType = UserMfaType.TOTP.name();

        final var userMfaType = UserMfaType.of(aType);

        Assertions.assertNotNull(userMfaType);
        Assertions.assertEquals(aType, userMfaType.get().name());
    }

    @Test
    void givenAValidValues_whenCallCreateMfaDevice_thenReturnUserMfa() {
        final var aMfaSecret = "mfaSecret";
        final var aDeviceName = "deviceName";
        final var aMfaType = UserMfaType.TOTP;

        final var aUserMfa = UserMfa.newMfa();

        final var userMfa = aUserMfa.createMfaOnDevice(aMfaSecret, aDeviceName, aMfaType);

        Assertions.assertNotNull(userMfa);
        Assertions.assertEquals(aMfaSecret, userMfa.getMfaSecret().get());
        Assertions.assertEquals(aDeviceName, userMfa.getDeviceName().get());
        Assertions.assertEquals(aMfaType, userMfa.getMfaType().get());
    }

    @Test
    void givenAValidValue_whenCallConfirmDevice_thenReturnUserMfa() {
        final var aValidUntil = InstantUtils.now().plus(5, ChronoUnit.MINUTES);

        final var aUserMfa = UserMfa.newMfa();

        final var userMfa = aUserMfa.confirmDevice(aValidUntil);

        Assertions.assertNotNull(userMfa);
        Assertions.assertEquals(aValidUntil, userMfa.getValidUntil().get());
        Assertions.assertTrue(userMfa.isDeviceVerified());
    }

    @Test
    void testCallVerifyMfa_whenCalled_thenReturnUserMfa() {
        final var aUserMfa = UserMfa.newMfa();

        final var userMfa = aUserMfa.verifyMfa();

        Assertions.assertNotNull(userMfa);
        Assertions.assertTrue(userMfa.isMfaVerified());
        Assertions.assertFalse(userMfa.isDeviceVerified());
        Assertions.assertTrue(userMfa.getValidUntil().isPresent());
    }

    @Test
    void givenAllValuesTrue_whenCallIsValid_thenReturnTrue() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .confirmDevice(InstantUtils.now().plus(5, ChronoUnit.MINUTES))
                .verifyMfa();

        final var isValid = aUserMfa.isValid();

        Assertions.assertTrue(isValid);
    }

    @Test
    void givenAllValuesFalse_whenCallIsValid_thenReturnFalse() {
        final var aUserMfa = UserMfa.newMfa();

        final var isValid = aUserMfa.isValid();

        Assertions.assertFalse(isValid);
    }

    @Test
    void givenEnabledTrueAndVerifiedFalse_whenCallIsValid_thenReturnFalse() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .updateValidUntil(InstantUtils.now().plus(5, ChronoUnit.MINUTES));

        final var isValid = aUserMfa.isValid();

        Assertions.assertFalse(isValid);
    }

    @Test
    void givenEnabledTrueAndVerifiedTrueAndDeviceVerifiedFalse_whenCallIsValid_thenReturnFalse() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .verifyMfa();

        final var isValid = aUserMfa.isValid();

        Assertions.assertFalse(isValid);
    }

    @Test
    void givenEnabledTrueAndVerifiedTrueAndDeviceVerifiedTrueAndValidUntilExpired_whenCallIsValid_thenReturnFalse() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .verifyMfa()
                .confirmDevice(InstantUtils.now().minus(5, ChronoUnit.MINUTES));

        final var isValid = aUserMfa.isValid();

        Assertions.assertFalse(isValid);
    }

    @Test
    void givenEnabledTrueAndVerifiedTrueAndDeviceVerifiedTrueAndValidUntilNotExpired_whenCallIsValid_thenReturnTrue() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .verifyMfa()
                .confirmDevice(InstantUtils.now().plus(5, ChronoUnit.MINUTES));

        final var isValid = aUserMfa.isValid();

        Assertions.assertTrue(isValid);
    }

    @Test
    void givenEnabledTrueAndVerifiedTrueAndDeviceVerifiedTrueAndValidUntilNotHaveDate_whenCallIsValid_thenReturnTrue() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .verifyMfa()
                .confirmDevice(InstantUtils.now().plus(5, ChronoUnit.MINUTES))
                .updateValidUntil(null);

        final var isValid = aUserMfa.isValid();

        Assertions.assertFalse(isValid);
    }

    @Test
    void givenAValidActiveUserMfa_whenCallDisableMfa_thenReturnUserMfa() {
        final var aUserMfa = UserMfa.newMfa()
                .createMfaOnDevice("mfaSecret", "deviceName", UserMfaType.TOTP)
                .verifyMfa()
                .confirmDevice(InstantUtils.now().plus(5, ChronoUnit.MINUTES));

        final var userMfa = aUserMfa.disableMfa();

        Assertions.assertNotNull(userMfa);
        Assertions.assertFalse(userMfa.isMfaEnabled());
        Assertions.assertTrue(userMfa.getMfaSecret().isEmpty());
        Assertions.assertTrue(userMfa.getDeviceName().isEmpty());
        Assertions.assertTrue(userMfa.getMfaType().isEmpty());
        Assertions.assertFalse(userMfa.isMfaVerified());
        Assertions.assertFalse(userMfa.isDeviceVerified());
        Assertions.assertTrue(userMfa.getValidUntil().isEmpty());
    }
}
