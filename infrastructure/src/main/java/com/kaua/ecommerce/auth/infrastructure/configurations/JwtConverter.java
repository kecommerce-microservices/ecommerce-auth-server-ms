package com.kaua.ecommerce.auth.infrastructure.configurations;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthoritiesConverter authoritiesConverter;

    public JwtConverter() {
        this.authoritiesConverter = new AuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull final Jwt jwt) {
        return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), extractPrincipal(jwt));
    }

    private String extractPrincipal(final Jwt jwt) {
        return jwt.getClaimAsString(JwtClaimNames.SUB);
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
        return this.authoritiesConverter.convert(jwt);
    }
}
