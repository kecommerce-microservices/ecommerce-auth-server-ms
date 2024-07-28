package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomPasswordGrantAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 7840626509676504832L;

    private final AuthorizationGrantType authorizationGrantType;
    private final Authentication clientPrincipal;
    private final Map<String, Object> additionalParameters;

    public CustomPasswordGrantAuthenticationToken(
            final AuthorizationGrantType authorizationGrantType,
            final Authentication clientPrincipal,
            final Map<String, Object> additionalParameters
    ) {
        super(Collections.emptyList());
        this.authorizationGrantType = authorizationGrantType;
        this.clientPrincipal = clientPrincipal;
        this.additionalParameters = Collections.unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters) : Collections.emptyMap());
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }

    public AuthorizationGrantType getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public Authentication getClientPrincipal() {
        return clientPrincipal;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }
}
