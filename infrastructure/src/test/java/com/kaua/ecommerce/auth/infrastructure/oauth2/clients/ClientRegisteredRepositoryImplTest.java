package com.kaua.ecommerce.auth.infrastructure.oauth2.clients;

import com.kaua.ecommerce.auth.infrastructure.AbstractCacheTest;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password.CustomPasswordGrantType;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

@DatabaseRepositoryTest
class ClientRegisteredRepositoryImplTest extends AbstractCacheTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ClientJpaEntityRepository clientJpaEntityRepository;

    @Autowired
    private ClientRegisteredRepositoryImpl clientRegisteredRepositoryImpl;

    @Test
    void givenAValidRegisteredClient_whenCallSave_thenSaveClient() {
        final var aClient = RegisteredClient.withId(IdentifierUtils.generateNewId())
                .clientId("client-id")
                .clientSecret("client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .authorizationGrantType(CustomPasswordGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .redirectUri("http://localhost:8080")
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings
                        .builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .refreshTokenTimeToLive(Duration.ofDays(1))
                        .reuseRefreshTokens(false)
                        .build())
                .build();

        Assertions.assertEquals(0, this.clientJpaEntityRepository.count());

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        Assertions.assertEquals(1, this.clientJpaEntityRepository.count());

        final var clientEntity = this.clientRegisteredRepositoryImpl.findByClientId(aClient.getClientId());

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClient.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClient.getClientSecret(), clientEntity.getClientSecret());
        Assertions.assertEquals(aClient.getAuthorizationGrantTypes().size(), clientEntity.getAuthorizationGrantTypes().size());
        Assertions.assertEquals(aClient.getClientAuthenticationMethods().size(), clientEntity.getClientAuthenticationMethods().size());
        Assertions.assertEquals(aClient.getRedirectUris().size(), clientEntity.getRedirectUris().size());
        Assertions.assertEquals(aClient.getScopes().size(), clientEntity.getScopes().size());
        Assertions.assertEquals(aClient.getClientSettings().isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClient.getClientSettings().isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aClient.getTokenSettings().getAccessTokenTimeToLive(), clientEntity.getTokenSettings().getAccessTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().getRefreshTokenTimeToLive(), clientEntity.getTokenSettings().getRefreshTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().isReuseRefreshTokens(), clientEntity.getTokenSettings().isReuseRefreshTokens());
    }

    @Test
    void givenAValidId_whenCallFindById_thenReturnRegisteredClient() {
        final var aClient = createRegisteredClient();

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        final var clientEntity = this.clientRegisteredRepositoryImpl.findById(aClient.getId());

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClient.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClient.getClientSecret(), clientEntity.getClientSecret());
        Assertions.assertEquals(aClient.getAuthorizationGrantTypes().size(), clientEntity.getAuthorizationGrantTypes().size());
        Assertions.assertEquals(aClient.getClientAuthenticationMethods().size(), clientEntity.getClientAuthenticationMethods().size());
        Assertions.assertEquals(aClient.getRedirectUris().size(), clientEntity.getRedirectUris().size());
        Assertions.assertEquals(aClient.getScopes().size(), clientEntity.getScopes().size());
        Assertions.assertEquals(aClient.getClientSettings().isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClient.getClientSettings().isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aClient.getTokenSettings().getAccessTokenTimeToLive(), clientEntity.getTokenSettings().getAccessTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().getRefreshTokenTimeToLive(), clientEntity.getTokenSettings().getRefreshTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().isReuseRefreshTokens(), clientEntity.getTokenSettings().isReuseRefreshTokens());
    }

    @Test
    void givenAValidClientId_whenCallFindByClientId_thenReturnRegisteredClient() {
        final var aClient = createRegisteredClient();

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        final var clientEntity = this.clientRegisteredRepositoryImpl.findByClientId(aClient.getClientId());

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClient.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClient.getClientSecret(), clientEntity.getClientSecret());
        Assertions.assertEquals(aClient.getAuthorizationGrantTypes().size(), clientEntity.getAuthorizationGrantTypes().size());
        Assertions.assertEquals(aClient.getClientAuthenticationMethods().size(), clientEntity.getClientAuthenticationMethods().size());
        Assertions.assertEquals(aClient.getRedirectUris().size(), clientEntity.getRedirectUris().size());
        Assertions.assertEquals(aClient.getScopes().size(), clientEntity.getScopes().size());
        Assertions.assertEquals(aClient.getClientSettings().isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClient.getClientSettings().isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aClient.getTokenSettings().getAccessTokenTimeToLive(), clientEntity.getTokenSettings().getAccessTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().getRefreshTokenTimeToLive(), clientEntity.getTokenSettings().getRefreshTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().isReuseRefreshTokens(), clientEntity.getTokenSettings().isReuseRefreshTokens());
    }

    @Test
    void givenAValidIdButExistsInCache_whenCallFindById_thenReturnRegisteredClient() {
        final var aClient = createRegisteredClient();

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        this.clientRegisteredRepositoryImpl.findById(aClient.getId());

        final var clientEntity = this.clientRegisteredRepositoryImpl.findById(aClient.getId());

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClient.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClient.getClientSecret(), clientEntity.getClientSecret());
        Assertions.assertEquals(aClient.getAuthorizationGrantTypes().size(), clientEntity.getAuthorizationGrantTypes().size());
        Assertions.assertEquals(aClient.getClientAuthenticationMethods().size(), clientEntity.getClientAuthenticationMethods().size());
        Assertions.assertEquals(aClient.getRedirectUris().size(), clientEntity.getRedirectUris().size());
        Assertions.assertEquals(aClient.getScopes().size(), clientEntity.getScopes().size());
        Assertions.assertEquals(aClient.getClientSettings().isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClient.getClientSettings().isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aClient.getTokenSettings().getAccessTokenTimeToLive(), clientEntity.getTokenSettings().getAccessTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().getRefreshTokenTimeToLive(), clientEntity.getTokenSettings().getRefreshTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().isReuseRefreshTokens(), clientEntity.getTokenSettings().isReuseRefreshTokens());
    }

    @Test
    void givenAValidIdButReturnNullJson_whenCallFindById_thenReturnRegisteredClient() {
        final var aClient = createRegisteredClient();

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        this.clientRegisteredRepositoryImpl.findById(aClient.getId());

        this.redisTemplate.delete("oauth2:clients:obj:" + aClient.getId());

        final var clientEntity = this.clientRegisteredRepositoryImpl.findById(aClient.getId());

        Assertions.assertNotNull(clientEntity);
    }

    @Test
    void givenAValidClientIdButExistsInCache_whenCallFindByClientId_thenReturnRegisteredClient() {
        final var aClient = createRegisteredClient();

        Assertions.assertDoesNotThrow(() -> this.clientRegisteredRepositoryImpl.save(aClient));

        this.clientRegisteredRepositoryImpl.findByClientId(aClient.getClientId());

        final var clientEntity = this.clientRegisteredRepositoryImpl.findByClientId(aClient.getClientId());

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClient.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClient.getClientSecret(), clientEntity.getClientSecret());
        Assertions.assertEquals(aClient.getAuthorizationGrantTypes().size(), clientEntity.getAuthorizationGrantTypes().size());
        Assertions.assertEquals(aClient.getClientAuthenticationMethods().size(), clientEntity.getClientAuthenticationMethods().size());
        Assertions.assertEquals(aClient.getRedirectUris().size(), clientEntity.getRedirectUris().size());
        Assertions.assertEquals(aClient.getScopes().size(), clientEntity.getScopes().size());
        Assertions.assertEquals(aClient.getClientSettings().isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClient.getClientSettings().isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aClient.getTokenSettings().getAccessTokenTimeToLive(), clientEntity.getTokenSettings().getAccessTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().getRefreshTokenTimeToLive(), clientEntity.getTokenSettings().getRefreshTokenTimeToLive());
        Assertions.assertEquals(aClient.getTokenSettings().isReuseRefreshTokens(), clientEntity.getTokenSettings().isReuseRefreshTokens());
    }

    private RegisteredClient createRegisteredClient() {
        return RegisteredClient.withId(IdentifierUtils.generateNewId())
                .clientId("client-id")
                .clientSecret("client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .authorizationGrantType(CustomPasswordGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .redirectUri("http://localhost:8080")
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings
                        .builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .refreshTokenTimeToLive(Duration.ofDays(1))
                        .reuseRefreshTokens(false)
                        .build())
                .build();
    }
}
