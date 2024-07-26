package com.kaua.ecommerce.auth.infrastructure.gateways;

import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BcryptCryptographyGateway implements CryptographyGateway {

    private final PasswordEncoder passwordEncoder;

    public BcryptCryptographyGateway(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public String encrypt(final String plainText) {
        return this.passwordEncoder.encode(plainText);
    }
}
