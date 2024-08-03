package com.kaua.ecommerce.auth.infrastructure.gateways;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.domain.exceptions.InternalServerErrorException;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.auth.infrastructure.constants.Constants;
import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Component
public class MfaGatewayImpl implements MfaGateway {

    // In future refactor to receive others mfas

    private final QRCodeWriter qrCodeWriter;
    private final KeysService keysService;

    public MfaGatewayImpl(
            final QRCodeWriter qrCodeWriter,
            final KeysService keysService
    ) {
        this.qrCodeWriter = Objects.requireNonNull(qrCodeWriter);
        this.keysService = Objects.requireNonNull(keysService);
    }

    @Override
    public String generateSecret(final UserMfaType type) {
        final var aGoogleAuth = new GoogleAuthenticator();
        return this.keysService.encrypt(
                aGoogleAuth.createCredentials().getKey(),
                Constants.MFA_PUBLIC_KEY
        );
    }

    @Override
    public boolean accepts(final UserMfaType type, final String code, final String secret) {
        final var aGoogleAuth = new GoogleAuthenticator();
        final var aParsedSecret = this.keysService.decrypt(
                secret,
                Constants.MFA_PRIVATE_KEY
        );
        return aGoogleAuth.authorize(aParsedSecret, Integer.parseInt(code));
    }

    @Override
    public String generateConfirmationQrCode(
            final String secret,
            final String email,
            final String issuer
    ) {
        final var aParsedSecret = this.keysService.decrypt(
                secret,
                Constants.MFA_PRIVATE_KEY
        );
        String uri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, email, aParsedSecret, issuer);

        try {
            final var aBitMatrix = this.qrCodeWriter
                    .encode(uri, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(aBitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return Base64.getEncoder().encodeToString(pngData);
        } catch (WriterException | IOException e) {
            throw new InternalServerErrorException("Error generating mfa QR code", e);
        }
    }
}
