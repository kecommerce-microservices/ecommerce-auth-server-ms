package com.kaua.ecommerce.auth.infrastructure.configurations.authentication;

import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

import java.util.Collection;

public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthoritiesConverter authoritiesConverter;

    public JwtConverter() {
        this.authoritiesConverter = new AuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull final Jwt jwt) {
        return new EcommerceUserAuthentication(
                extractAuthorities(jwt),
                jwt,
                extractPrincipal(jwt)
        );
    }

    private UserDetailsImpl extractPrincipal(final Jwt jwt) {
        return new UserDetailsImpl(
                jwt.getClaimAsString(JwtClaimNames.SUB),
                null,
                extractAuthorities(jwt)
        );
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
        return this.authoritiesConverter.convert(jwt);
    }
}
