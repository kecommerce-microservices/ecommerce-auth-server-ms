package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public final class CustomPasswordGrantType {

    private CustomPasswordGrantType() {}

    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");
}
