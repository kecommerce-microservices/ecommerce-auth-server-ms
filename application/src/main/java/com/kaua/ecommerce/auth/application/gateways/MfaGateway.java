package com.kaua.ecommerce.auth.application.gateways;

import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;

public interface MfaGateway {

    String generateSecret(UserMfaType type);

    boolean accepts(UserMfaType type, String code, String secret);

    String generateConfirmationQrCode(String secret, String email, String issuer);
}
