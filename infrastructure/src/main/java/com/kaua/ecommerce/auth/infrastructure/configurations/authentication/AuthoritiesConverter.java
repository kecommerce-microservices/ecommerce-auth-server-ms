package com.kaua.ecommerce.auth.infrastructure.configurations.authentication;

import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.utils.CustomTokenClaimsUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(@NonNull final Jwt jwt) {
        final var resourceAuthorities = extractAuthorities(jwt);

        if (resourceAuthorities.isEmpty()) {
            return Collections.emptySet();
        }

        return resourceAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private List<String> extractAuthorities(final Jwt jwt) {
        return jwt.getClaimAsStringList(CustomTokenClaimsUtils.AUTHORITIES) == null
                ? Collections.emptyList()
                : jwt.getClaimAsStringList(CustomTokenClaimsUtils.AUTHORITIES);
    }
}
