package com.kaua.ecommerce.auth.infrastructure.userdetails;

import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserDetailsImpl extends User {

    public UserDetailsImpl(
            final UserJpaEntity aEntity,
            final Collection<GrantedAuthority> authorities
    ) {
        super(aEntity.getId().toString(), aEntity.getPassword(), authorities);
    }
}
