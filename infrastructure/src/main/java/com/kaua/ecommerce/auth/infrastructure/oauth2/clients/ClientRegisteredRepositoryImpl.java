package com.kaua.ecommerce.auth.infrastructure.oauth2.clients;

import com.kaua.ecommerce.auth.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientEntity;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class ClientRegisteredRepositoryImpl implements RegisteredClientRepository {

    private static final Logger log = LoggerFactory.getLogger(ClientRegisteredRepositoryImpl.class);

    private static final String OAUTH2_CLIENTS_REF_KEY_PREFIX = "oauth2:clients:ref:";
    private static final String OAUTH2_CLIENTS_OBJECT_KEY_PREFIX = "oauth2:clients:obj:";

    private static final String OAUTH2_CLIENTS_REF_KEY_ID = "id:";
    private static final String OAUTH2_CLIENTS_REF_KEY_CLIENT_ID = "clientId:";

    private static final long EXPIRATION_TIME = 5; // 5 days
    private static final TimeUnit EXPIRATION_TIME_UNIT = TimeUnit.DAYS;

    private final ClientJpaEntityRepository clientJpaEntityRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public ClientRegisteredRepositoryImpl(
            final ClientJpaEntityRepository clientJpaEntityRepository,
            final RedisTemplate<String, String> redisTemplate
    ) {
        this.clientJpaEntityRepository = Objects.requireNonNull(clientJpaEntityRepository);
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public void save(final RegisteredClient registeredClient) {
        final var aClientEntity = ClientEntity.create(registeredClient);
        this.clientJpaEntityRepository.save(aClientEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public RegisteredClient findById(final String id) {
        final var aRefKey = OAUTH2_CLIENTS_REF_KEY_PREFIX
                .concat(OAUTH2_CLIENTS_REF_KEY_CLIENT_ID)
                .concat(id);
        return this.findClientByRefKey(aRefKey, () -> this.clientJpaEntityRepository.findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public RegisteredClient findByClientId(final String clientId) {
        final var aRefKey = OAUTH2_CLIENTS_REF_KEY_PREFIX
                .concat(OAUTH2_CLIENTS_REF_KEY_CLIENT_ID)
                .concat(clientId);
        return this.findClientByRefKey(aRefKey, () -> this.clientJpaEntityRepository.findByClientId(clientId));
    }

    private RegisteredClient findClientByRefKey(
            final String refKey,
            final Supplier<Optional<ClientEntity>> repositoryFinder
    ) {
        final var aActualKey = this.redisTemplate.opsForValue().get(refKey);

        if (aActualKey != null) {
            final var aClientJson = this.redisTemplate.opsForValue().get(aActualKey);

            if (aClientJson != null) {
                log.debug("Client found in Redis using key {}", refKey);
                final var aClient = Json.readValue(aClientJson, ClientEntity.class);
                return ClientEntity.fromClient(aClient);
            }
        }

        return repositoryFinder.get()
                .map(it -> {
                    final var aClientJson = Json.writeValueAsString(it);
                    final var aObjectKey = OAUTH2_CLIENTS_OBJECT_KEY_PREFIX + it.getId();

                    // save client object with key
                    this.redisTemplate.opsForValue().set(aObjectKey, aClientJson);

                    final var aIdRefKey = OAUTH2_CLIENTS_REF_KEY_PREFIX
                            .concat(OAUTH2_CLIENTS_REF_KEY_ID)
                            .concat(it.getId());

                    final var aClientIdRefKey = OAUTH2_CLIENTS_REF_KEY_PREFIX
                            .concat(OAUTH2_CLIENTS_REF_KEY_CLIENT_ID)
                            .concat(it.getClientId());

                    // save id ref key
                    this.redisTemplate.opsForValue()
                            .set(aIdRefKey, aObjectKey);

                    // save clientId ref key
                    this.redisTemplate.opsForValue()
                            .set(aClientIdRefKey, aObjectKey);

                    this.redisTemplate.expire(aObjectKey, EXPIRATION_TIME, EXPIRATION_TIME_UNIT);
                    this.redisTemplate.expire(aIdRefKey, EXPIRATION_TIME, EXPIRATION_TIME_UNIT);
                    this.redisTemplate.expire(aClientIdRefKey, EXPIRATION_TIME, EXPIRATION_TIME_UNIT);

                    return it;
                })
                .map(ClientEntity::fromClient)
                .orElseThrow(NotFoundException.with("OAuth2Client", getIdFromRefKey(refKey)));
    }

    private String getIdFromRefKey(final String refKey) {
        if (refKey.startsWith(OAUTH2_CLIENTS_REF_KEY_PREFIX.concat(OAUTH2_CLIENTS_REF_KEY_ID))) {
            return refKey.substring((OAUTH2_CLIENTS_REF_KEY_PREFIX.concat(OAUTH2_CLIENTS_REF_KEY_ID)).length());
        }
        return refKey.substring((OAUTH2_CLIENTS_REF_KEY_PREFIX.concat(OAUTH2_CLIENTS_REF_KEY_CLIENT_ID)).length());
    }
}
