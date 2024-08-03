package com.kaua.ecommerce.auth.infrastructure.gateways;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.exceptions.InternalServerErrorException;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.auth.infrastructure.IntegrationTest;
import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@IntegrationTest
public class MfaGatewayImplTest {

    @MockBean
    private KeysService keysService;

    @Test
    void givenAValidUserMfaType_whenCallGenerateSecret_thenReturnAValidSecret() {
        final var aMfaType = UserMfaType.TOTP;

        final var mfaGatewayImpl = new MfaGatewayImpl(new QRCodeWriter(), keysService);

        Mockito.when(keysService.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());

        final var aSecret = mfaGatewayImpl.generateSecret(aMfaType);

        Assertions.assertNotNull(aSecret);
    }

    @Test
    void givenAValidValues_whenCallAccepts_thenReturnFalse() {
        final var mfaGatewayImpl = new MfaGatewayImpl(new QRCodeWriter(), keysService);

        Mockito.when(keysService.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(keysService.decrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());

        final var aMfaType = UserMfaType.TOTP;
        final var aCode = "123456";
        final var aSecret = mfaGatewayImpl.generateSecret(aMfaType);

        final var aResult = mfaGatewayImpl.accepts(aMfaType, aCode, aSecret);

        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAValidValues_whenCallGenerateConfirmationQrCode_thenReturnAValidQrCode() {
        final var mfaGatewayImpl = new MfaGatewayImpl(new QRCodeWriter(), keysService);

        Mockito.when(keysService.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(keysService.decrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());

        final var aSecret = mfaGatewayImpl.generateSecret(UserMfaType.TOTP);
        final var aEmail = Fixture.Users.email();
        final var aIssuer = "deviceName";

        final var aQrCode = mfaGatewayImpl.generateConfirmationQrCode(aSecret, aEmail, aIssuer);

        Assertions.assertNotNull(aQrCode);
    }

    // test generate confirmation qrcode throws
    @Test
    void givenAnInvalidValues_whenCallGenerateConfirmationQrCode_thenThrowsRuntimeException() throws WriterException {
        final var aSecret = "secret";
        final var aEmail = Fixture.Users.email();
        final var aIssuer = "deviceName";

        final var qrCodeWriter = Mockito.mock(QRCodeWriter.class);
        final var mfaGatewayImpl = new MfaGatewayImpl(qrCodeWriter, keysService);

        Mockito.when(keysService.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(returnsFirstArg());
        Mockito.when(qrCodeWriter.encode(Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new WriterException());

        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            mfaGatewayImpl.generateConfirmationQrCode(aSecret, aEmail, aIssuer);
        });
    }
}
