package com.kaua.ecommerce.auth.infrastructure.userdetails;

import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaEntityRepository userJpaEntityRepository;
    private final RoleJpaEntityRepository roleJpaEntityRepository;

    public UserDetailsServiceImpl(
            final UserJpaEntityRepository userJpaEntityRepository,
            final RoleJpaEntityRepository roleJpaEntityRepository
    ) {
        this.userJpaEntityRepository = Objects.requireNonNull(userJpaEntityRepository);
        this.roleJpaEntityRepository = Objects.requireNonNull(roleJpaEntityRepository);
    }

    // in this code have 2 calls to DB, one to get the user and another to get the roles

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var aUser = this.userJpaEntityRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        if (aUser.isDeleted()) {
            throw new UsernameNotFoundException("User is deleted");
        }

        return new UserDetailsImpl(
                aUser.getId().toString(),
                aUser.getPassword(),
                getAuthorities(aUser)
        );
    }

    private Collection<GrantedAuthority> getAuthorities(final UserJpaEntity aUser) {
        final var aRolesIds = aUser.getRoles().stream()
                .map(it -> it.getId().getRoleId())
                .toList();

        return this.roleJpaEntityRepository.findAllById(aRolesIds)
                .stream()
                .map(it -> new SimpleGrantedAuthority(it.getName()))
                .collect(Collectors.toSet());
    }
}
