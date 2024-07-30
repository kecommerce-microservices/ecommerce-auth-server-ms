package com.kaua.ecommerce.auth.infrastructure.oauth2.clients;

import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientEntity;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class ClientRegisteredRepositoryImpl implements RegisteredClientRepository {

    private final ClientJpaEntityRepository clientJpaEntityRepository;

    public ClientRegisteredRepositoryImpl(final ClientJpaEntityRepository clientJpaEntityRepository) {
        this.clientJpaEntityRepository = Objects.requireNonNull(clientJpaEntityRepository);
    }

    // TODO: talvez adicionar um cache, assim evitar ficar batendo no db para pegar o client

    @Override
    public void save(final RegisteredClient registeredClient) {
        final var aClientEntity = ClientEntity.create(registeredClient);
        this.clientJpaEntityRepository.save(aClientEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public RegisteredClient findById(final String id) {
        return this.clientJpaEntityRepository.findById(id)
                .map(ClientEntity::fromClient)
                .orElseThrow(NotFoundException.with("OAuth2Client", id));
    }

    @Transactional(readOnly = true)
    @Override
    public RegisteredClient findByClientId(final String clientId) {
        return this.clientJpaEntityRepository.findByClientId(clientId)
                .map(ClientEntity::fromClient)
                .orElseThrow(NotFoundException.with("OAuth2Client", clientId));
    }
}
