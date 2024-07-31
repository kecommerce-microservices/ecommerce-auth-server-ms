package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@DatabaseRepositoryTest
class ClientJpaEntityRepositoryTest {

    @Autowired
    private ClientJpaEntityRepository clientJpaEntityRepository;

    @Test
    void givenAValidValues_whenCallSave_thenSaveClient() {
        final var aClientEntity = new ClientEntity();
        aClientEntity.setId(IdentifierUtils.generateNewId());
        aClientEntity.setClientId("client-id");
        aClientEntity.setSecret("client-secret");

        final var aAuthenticationMethod = new AuthenticationMethodEntity();
        aAuthenticationMethod.setId(IdentifierUtils.generateNewId());
        aAuthenticationMethod.setAuthenticationMethod("client-secret-basic");
        aAuthenticationMethod.setClient(aClientEntity);

        final var aGrantType = new GrantTypeEntity();
        aGrantType.setId(IdentifierUtils.generateNewId());
        aGrantType.setGrantType("password");
        aGrantType.setClient(aClientEntity);

        final var aRedirectUrl = new RedirectUrlEntity();
        aRedirectUrl.setId(IdentifierUtils.generateNewId());
        aRedirectUrl.setUrl("http://localhost:8080");
        aRedirectUrl.setClient(aClientEntity);

        final var aScope = new ScopeEntity();
        aScope.setId(IdentifierUtils.generateNewId());
        aScope.setScope("read");
        aScope.setClient(aClientEntity);

        final var aClientEntitySettings = new ClientSettingsEntity();
        aClientEntitySettings.setId(IdentifierUtils.generateNewId());
        aClientEntitySettings.setRequireProofKey(false);
        aClientEntitySettings.setRequireAuthorizationConsent(false);
        aClientEntitySettings.setClient(aClientEntity);

        final var aTokenSettings = new ClientTokenSettingsEntity();
        aTokenSettings.setId(IdentifierUtils.generateNewId());
        aTokenSettings.setAccessTokenTTL(5);
        aTokenSettings.setRefreshTokenTTL(1);
        aTokenSettings.setReuseRefreshTokens(false);
        aTokenSettings.setClient(aClientEntity);

        aClientEntity.setAuthenticationMethods(Set.of(aAuthenticationMethod));
        aClientEntity.setGrantTypes(Set.of(aGrantType));
        aClientEntity.setRedirectUrls(Set.of(aRedirectUrl));
        aClientEntity.setScopes(Set.of(aScope));
        aClientEntity.setClientSettings(aClientEntitySettings);
        aClientEntity.setClientTokenSettings(aTokenSettings);

        this.clientJpaEntityRepository.save(aClientEntity);

        final var clientEntity = this.clientJpaEntityRepository.findByClientId(aClientEntity.getClientId()).get();

        Assertions.assertNotNull(clientEntity);
        Assertions.assertEquals(aClientEntity.getClientId(), clientEntity.getClientId());
        Assertions.assertEquals(aClientEntity.getSecret(), clientEntity.getSecret());
        Assertions.assertEquals(aAuthenticationMethod.getId(), clientEntity.getAuthenticationMethods().iterator().next().getId());
        Assertions.assertEquals(aAuthenticationMethod.getAuthenticationMethod(), clientEntity.getAuthenticationMethods().iterator().next().getAuthenticationMethod());
        Assertions.assertEquals(aAuthenticationMethod.getAuthenticationMethod(), clientEntity.getAuthenticationMethods().iterator().next().getAuthenticationMethod());
        Assertions.assertNotNull(aAuthenticationMethod.getClient());
        Assertions.assertEquals(aGrantType.getId(), clientEntity.getGrantTypes().iterator().next().getId());
        Assertions.assertEquals(aGrantType.getGrantType(), clientEntity.getGrantTypes().iterator().next().getGrantType());
        Assertions.assertNotNull(aGrantType.getClient());
        Assertions.assertEquals(aRedirectUrl.getId(), clientEntity.getRedirectUrls().iterator().next().getId());
        Assertions.assertEquals(aRedirectUrl.getUrl(), clientEntity.getRedirectUrls().iterator().next().getUrl());
        Assertions.assertNotNull(aRedirectUrl.getClient());
        Assertions.assertEquals(aScope.getId(), clientEntity.getScopes().iterator().next().getId());
        Assertions.assertEquals(aScope.getScope(), clientEntity.getScopes().iterator().next().getScope());
        Assertions.assertNotNull(aScope.getClient());
        Assertions.assertEquals(aClientEntitySettings.getId(), clientEntity.getClientSettings().getId());
        Assertions.assertEquals(aClientEntitySettings.isRequireAuthorizationConsent(), clientEntity.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aClientEntitySettings.isRequireProofKey(), clientEntity.getClientSettings().isRequireProofKey());
        Assertions.assertNotNull(aClientEntitySettings.getClient());
        Assertions.assertEquals(aTokenSettings.getId(), clientEntity.getClientTokenSettings().getId());
        Assertions.assertEquals(aTokenSettings.getAccessTokenTTL(), clientEntity.getClientTokenSettings().getAccessTokenTTL());
        Assertions.assertEquals(aTokenSettings.getRefreshTokenTTL(), clientEntity.getClientTokenSettings().getRefreshTokenTTL());
        Assertions.assertEquals(aTokenSettings.isReuseRefreshTokens(), clientEntity.getClientTokenSettings().isReuseRefreshTokens());
        Assertions.assertNotNull(aTokenSettings.getClient());
    }
}
