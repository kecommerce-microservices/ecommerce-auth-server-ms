package com.kaua.ecommerce.auth.infrastructure.gateways;

import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class BcryptCryptographyGatewayTest {

    @Autowired
    private CryptographyGateway cryptographyGateway;

    @Test
    void givenAValidPlainText_whenHash_thenReturnHashedText() {
        final String plainText = "plainText";
        final String hashedText = this.cryptographyGateway.encrypt(plainText);

        Assertions.assertNotNull(hashedText);
        Assertions.assertNotEquals(plainText, hashedText);
    }
}
