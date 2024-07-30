package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import jakarta.persistence.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false)
    private String secret;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AuthenticationMethodEntity> authenticationMethods;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<GrantTypeEntity> grantTypes;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<RedirectUrlEntity> redirectUrls;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<ScopeEntity> scopes;

    @OneToOne(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private ClientTokenSettingsEntity clientTokenSettings;

    @OneToOne(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private ClientSettingsEntity clientSettings;

    public ClientEntity() {
    }

    private ClientEntity(
            final String id,
            final String clientId,
            final String secret,
            final Set<AuthenticationMethodEntity> authenticationMethods,
            final Set<GrantTypeEntity> grantTypes,
            final Set<RedirectUrlEntity> redirectUrls,
            final Set<ScopeEntity> scopes,
            final ClientTokenSettingsEntity clientTokenSettings,
            final ClientSettingsEntity clientSettings
    ) {
        this.id = id;
        this.clientId = clientId;
        this.secret = secret;
        this.authenticationMethods = authenticationMethods;
        this.grantTypes = grantTypes;
        this.redirectUrls = redirectUrls;
        this.scopes = scopes;
        this.clientTokenSettings = clientTokenSettings;
        this.clientSettings = clientSettings;
    }

    public static ClientEntity create(final RegisteredClient aClient) {
        final var aClientEntity = new ClientEntity(
                aClient.getId(),
                aClient.getClientId(),
                aClient.getClientSecret(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                null,
                null
        );

        aClient.getClientAuthenticationMethods()
                .forEach(aClientEntity::addClientAuthenticationMethod);

        aClient.getAuthorizationGrantTypes()
                .forEach(aClientEntity::addGrantType);

        aClient.getRedirectUris()
                .forEach(aClientEntity::addRedirectUrl);

        aClient.getScopes()
                .forEach(aClientEntity::addScope);

        aClientEntity.setClientSettings(new ClientSettingsEntity(
                IdentifierUtils.generateNewId(),
                aClient.getClientSettings().isRequireAuthorizationConsent(),
                aClient.getClientSettings().isRequireProofKey(),
                aClientEntity
        ));

        aClientEntity.setClientTokenSettings(new ClientTokenSettingsEntity(
                IdentifierUtils.generateNewId(),
                aClient.getTokenSettings().getAccessTokenTimeToLive().toMinutes(),
                aClient.getTokenSettings().getRefreshTokenTimeToLive().toDaysPart(),
                aClient.getTokenSettings().isReuseRefreshTokens(),
                aClientEntity
        ));

        return aClientEntity;
    }

    public static RegisteredClient fromClient(final ClientEntity client) {
        return RegisteredClient.withId(client.getId())
                .clientId(client.getClientId())
                .clientSecret(client.getSecret())
                .clientAuthenticationMethods(clientAuthenticationMethods(client.getAuthenticationMethods()))
                .authorizationGrantTypes(authorizationGrantTypes(client.getGrantTypes()))
                .scopes(scopes(client.getScopes()))
                .redirectUris(redirectUris(client.getRedirectUrls()))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(client.getClientSettings().isRequireAuthorizationConsent())
                        .requireProofKey(client.getClientSettings().isRequireProofKey())
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(client.getClientTokenSettings().getAccessTokenTTL()))
                        .refreshTokenTimeToLive(Duration.ofDays(client.getClientTokenSettings().getRefreshTokenTTL()))
                        .reuseRefreshTokens(client.getClientTokenSettings().isReuseRefreshTokens())
                        .build())
                .build();
    }

    private static Consumer<Set<AuthorizationGrantType>> authorizationGrantTypes(Set<GrantTypeEntity> grantTypes) {
        return s -> {
            for (GrantTypeEntity g : grantTypes) {
                s.add(new AuthorizationGrantType(g.getGrantType()));
            }
        };
    }

    private static Consumer<Set<ClientAuthenticationMethod>> clientAuthenticationMethods(
            Set<AuthenticationMethodEntity> authenticationMethods) {
        return m -> {
            for (AuthenticationMethodEntity a : authenticationMethods) {
                m.add(new ClientAuthenticationMethod(a.getAuthenticationMethod()));
            }
        };
    }

    private static Consumer<Set<String>> scopes(Set<ScopeEntity> scopes) {
        return s -> {
            for (ScopeEntity x : scopes) {
                s.add(x.getScope());
            }
        };
    }

    private static Consumer<Set<String>> redirectUris(Set<RedirectUrlEntity> uris) {
        return r -> {
            for (RedirectUrlEntity u : uris) {
                r.add(u.getUrl());
            }
        };
    }

    private void addClientAuthenticationMethod(final ClientAuthenticationMethod method) {
        this.authenticationMethods.add(AuthenticationMethodEntity.from(method, this));
    }

    private void addGrantType(final AuthorizationGrantType grantType) {
        this.grantTypes.add(GrantTypeEntity.from(grantType, this));
    }

    private void addRedirectUrl(final String url) {
        this.redirectUrls.add(RedirectUrlEntity.from(url, this));
    }

    private void addScope(final String scope) {
        this.scopes.add(ScopeEntity.from(scope, this));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Set<AuthenticationMethodEntity> getAuthenticationMethods() {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(Set<AuthenticationMethodEntity> authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    public Set<GrantTypeEntity> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(Set<GrantTypeEntity> grantTypes) {
        this.grantTypes = grantTypes;
    }

    public Set<RedirectUrlEntity> getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(Set<RedirectUrlEntity> redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    public Set<ScopeEntity> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ScopeEntity> scopes) {
        this.scopes = scopes;
    }

    public ClientTokenSettingsEntity getClientTokenSettings() {
        return clientTokenSettings;
    }

    public void setClientTokenSettings(ClientTokenSettingsEntity clientTokenSettings) {
        this.clientTokenSettings = clientTokenSettings;
    }

    public ClientSettingsEntity getClientSettings() {
        return clientSettings;
    }

    public void setClientSettings(ClientSettingsEntity clientSettings) {
        this.clientSettings = clientSettings;
    }
}
