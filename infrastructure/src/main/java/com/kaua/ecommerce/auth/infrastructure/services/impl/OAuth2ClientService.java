package com.kaua.ecommerce.auth.infrastructure.services.impl;

import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientEntity;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OAuth2ClientService {

    private final PasswordEncoder passwordEncoder;
    private final ClientJpaEntityRepository clientJpaEntityRepository;

    public OAuth2ClientService(
            final PasswordEncoder passwordEncoder,
            final ClientJpaEntityRepository clientJpaEntityRepository
    ) {
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.clientJpaEntityRepository = Objects.requireNonNull(clientJpaEntityRepository);
    }

    public ClientEntity saveClient(final CreateOAuth2ClientRequest request) {
        if (this.clientJpaEntityRepository.existsByClientId(request.clientId())) {
            throw DomainException.with("OAuth2 client with clientId already exists");
        }

        final var aHashedSecret = this.passwordEncoder.encode(request.clientSecret());

        return this.clientJpaEntityRepository.save(request.toEntity(aHashedSecret));
    }

    public void deleteClient(final String id) {
        if (this.clientJpaEntityRepository.count() == 1) {
            throw DomainException.with("Cannot delete the last oauth2 client");
        }

        this.clientJpaEntityRepository.deleteById(id);
    }
}
