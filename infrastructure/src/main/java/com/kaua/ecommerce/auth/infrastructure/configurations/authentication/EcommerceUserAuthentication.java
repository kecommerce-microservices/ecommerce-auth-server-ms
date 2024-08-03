package com.kaua.ecommerce.auth.infrastructure.configurations.authentication;

import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class EcommerceUserAuthentication extends AbstractAuthenticationToken {

    private final Jwt jwt;
    private final UserDetailsImpl userDetails;

    public EcommerceUserAuthentication(Collection<? extends GrantedAuthority> authorities, Jwt jwt, UserDetailsImpl userDetails) {
        super(authorities);
        this.jwt = jwt;
        this.userDetails = userDetails;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
