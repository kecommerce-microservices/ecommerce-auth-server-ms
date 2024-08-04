package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class MfaAuthenticationConverter implements AuthenticationConverter {

    private final Authentication initial;

    public MfaAuthenticationConverter(Authentication initial) {
        this.initial = initial;
    }

    @Override
    public Authentication convert(final HttpServletRequest request) {
        final var aOtpCode = request.getParameter("otp_code");

        if (aOtpCode == null || aOtpCode.isBlank()) {
            return null;
        }

        return new MfaAuthentication(aOtpCode, this.initial);
    }
}
