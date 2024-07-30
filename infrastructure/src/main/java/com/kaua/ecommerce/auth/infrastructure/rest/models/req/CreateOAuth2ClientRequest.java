package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.*;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;

import java.util.List;
import java.util.stream.Collectors;

public record CreateOAuth2ClientRequest(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("auth_methods") List<String> authMethods,
        @JsonProperty("grant_types") List<String> grantTypes,
        @JsonProperty("redirect_uris") List<String> redirectUris,
        @JsonProperty("scopes") List<String> scopes,
        @JsonProperty("client_settings") CreateOAuth2ClientSettingsRequest clientSettings,
        @JsonProperty("token_settings") CreateOAuth2ClientTokenRequest tokenSettings
) {

    public record CreateOAuth2ClientSettingsRequest(
            @JsonProperty("require_authorization_consent") boolean requireAuthorizationConsent,
            @JsonProperty("require_proof_key") boolean requireProofKey
    ) {}

    public record CreateOAuth2ClientTokenRequest(
            @JsonProperty("access_token_validity_minutes") long accessTokenValidityMinutes,
            @JsonProperty("refresh_token_validity_days") long refreshTokenValidityDays,
            @JsonProperty("reuse_refresh_tokens") boolean reuseRefreshTokens
    ) {}

    public ClientEntity toEntity(final String hashedSecret) {
        final var aClientEntity = new ClientEntity();
        aClientEntity.setId(IdentifierUtils.generateNewId());
        aClientEntity.setClientId(clientId);
        aClientEntity.setSecret(hashedSecret);

        aClientEntity.setAuthenticationMethods(authMethods
                .stream().map(it -> new AuthenticationMethodEntity(
                        IdentifierUtils.generateNewId(),
                        it,
                        aClientEntity
                ))
                .collect(Collectors.toSet()));

        aClientEntity.setGrantTypes(grantTypes
                .stream().map(it -> new GrantTypeEntity(
                        IdentifierUtils.generateNewId(),
                        it,
                        aClientEntity
                )).collect(Collectors.toSet()));

        aClientEntity.setRedirectUrls(redirectUris
                .stream().map(it -> new RedirectUrlEntity(
                        IdentifierUtils.generateNewId(),
                        it,
                        aClientEntity
                )).collect(Collectors.toSet()));

        aClientEntity.setScopes(scopes
                .stream().map(it -> new ScopeEntity(
                        IdentifierUtils.generateNewId(),
                        it,
                        aClientEntity
                )).collect(Collectors.toSet()));

        aClientEntity.setClientSettings(new ClientSettingsEntity(
                IdentifierUtils.generateNewId(),
                clientSettings.requireAuthorizationConsent(),
                clientSettings.requireProofKey(),
                aClientEntity
        ));

        aClientEntity.setClientTokenSettings(new ClientTokenSettingsEntity(
                IdentifierUtils.generateNewId(),
                tokenSettings.accessTokenValidityMinutes(),
                tokenSettings.refreshTokenValidityDays(),
                tokenSettings.reuseRefreshTokens(),
                aClientEntity
        ));

        return aClientEntity;
    }
}
