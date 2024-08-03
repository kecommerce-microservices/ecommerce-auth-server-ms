package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public record MfaAuthentication(
        String code,
        Authentication initial
) implements Authentication {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getDetails() {
        return initial.getDetails();
    }

    @Override
    public Object getPrincipal() {
        return initial.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        return initial.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        initial.setAuthenticated(isAuthenticated);
    }

    @Override
    public String getName() {
        return initial.getName();
    }
}
