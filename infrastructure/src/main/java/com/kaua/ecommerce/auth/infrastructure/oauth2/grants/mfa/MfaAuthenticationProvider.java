package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa;

import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;
import java.util.UUID;

public class MfaAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final MfaGateway mfaGateway;

    public MfaAuthenticationProvider(
            final UserRepository userRepository,
            final MfaGateway mfaGateway
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.mfaGateway = Objects.requireNonNull(mfaGateway);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final var aAuth = (MfaAuthentication) authentication;
        final var aUser = this.userRepository.findById(UUID.fromString(aAuth.getName()))
                .orElseThrow(NotFoundException.with(User.class, aAuth.getName()));
        final var aUserMfa = aUser.getMfa();

        if (aUserMfa.isMfaEnabled() && !aUserMfa.isValid()) {
            switch (aUserMfa.getMfaType().get()) {
                case TOTP, EMAIL -> {
                    if (!this.mfaGateway.accepts(UserMfaType.TOTP, aAuth.code(), aUserMfa.getMfaSecret().get())) {
                        throw new BadCredentialsException("Invalid MFA code");
                    }
                    aUserMfa.verifyMfa();
                    this.userRepository.save(aUser);
                }
            }
        }

        return aAuth.initial();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MfaAuthentication.class.isAssignableFrom(authentication);
    }
}
