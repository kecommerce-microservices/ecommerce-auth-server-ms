package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence.*;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class AuthorizationServiceRepositoryImpl implements OAuth2AuthorizationService {

    private final AuthorizationJpaEntityRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthorizationServiceRepositoryImpl(
            final AuthorizationJpaEntityRepository authorizationRepository,
            final RegisteredClientRepository registeredClientRepository
    ) {
        this.authorizationRepository = Objects.requireNonNull(authorizationRepository);
        this.registeredClientRepository = Objects.requireNonNull(registeredClientRepository);
        ClassLoader classLoader = AuthorizationServiceRepositoryImpl.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.mapper.registerModules(securityModules);
        this.mapper.registerModules(new OAuth2AuthorizationServerJackson2Module());
        this.mapper.registerModules(new CoreJackson2Module());
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);              // Custom authorization grant type
    }

    @Transactional
    @Override
    public void save(final OAuth2Authorization authorization) {
        final var aAuthEntity = new AuthorizationEntity();
        aAuthEntity.setId(authorization.getId());
        aAuthEntity.setRegisteredClientId(authorization.getRegisteredClientId());
        aAuthEntity.setPrincipalName(authorization.getPrincipalName());

        aAuthEntity.setAuthorizationGrantType(authorization.
                getAuthorizationGrantType().getValue());

        aAuthEntity.setAuthorizedScopes(StringUtils.collectionToDelimitedString(
                authorization.getAuthorizedScopes(), ","));

        aAuthEntity.setAttributes(writeString(authorization.getAttributes()));
        aAuthEntity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        final OAuth2Authorization.Token<OAuth2AuthorizationCode> aAuthCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (aAuthCode != null) {
            final var aAuthCodeEntity = new AuthorizationCodeEntity();
            aAuthCodeEntity.setId(aAuthCode.getToken().getTokenValue());
            aAuthCodeEntity.setAuthorization(aAuthEntity);
            aAuthCodeEntity.setIssuedAt(aAuthCode.getToken().getIssuedAt());
            aAuthCodeEntity.setExpiresAt(aAuthCode.getToken().getExpiresAt());
            aAuthCodeEntity.setMetadata(writeString(aAuthCode.getMetadata()));
            aAuthCodeEntity.setAuthorization(aAuthEntity);
            aAuthEntity.setAuthorizationCode(aAuthCodeEntity);
        }

        final OAuth2Authorization.Token<OAuth2AccessToken> aAccessToken = authorization.getToken(OAuth2AccessToken.class);
        if (aAccessToken != null) {
            final var aAccessTokenEntity = new AccessTokenEntity();
            aAccessTokenEntity.setId(IdentifierUtils.generateNewId());
            aAccessTokenEntity.setValue(aAccessToken.getToken().getTokenValue());
            aAccessTokenEntity.setIssuedAt(aAccessToken.getToken().getIssuedAt());
            aAccessTokenEntity.setExpiresAt(aAccessToken.getToken().getExpiresAt());
            aAccessTokenEntity.setMetadata(writeString(aAccessToken.getMetadata()));
            aAccessTokenEntity.setType(aAccessToken.getToken().getTokenType().getValue());
            aAccessTokenEntity.setAuthorization(aAuthEntity);

            if (aAccessToken.getToken().getScopes() != null) {
                aAccessTokenEntity.setScopes(StringUtils.collectionToDelimitedString(
                        aAccessToken.getToken().getScopes(), ","));
            }

            aAuthEntity.setAccessToken(aAccessTokenEntity);
        }

        final OAuth2Authorization.Token<OAuth2RefreshToken> aRefreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (aRefreshToken != null) {
            final var aRefreshTokenEntity = new RefreshTokenEntity();
            aRefreshTokenEntity.setId(IdentifierUtils.generateNewId());
            aRefreshTokenEntity.setValue(aRefreshToken.getToken().getTokenValue());
            aRefreshTokenEntity.setIssuedAt(aRefreshToken.getToken().getIssuedAt());
            aRefreshTokenEntity.setExpiresAt(aRefreshToken.getToken().getExpiresAt());
            aRefreshTokenEntity.setMetadata(writeString(aRefreshToken.getMetadata()));
            aRefreshTokenEntity.setAuthorization(aAuthEntity);
            aAuthEntity.setRefreshToken(aRefreshTokenEntity);
        }

        final OAuth2Authorization.Token<OidcIdToken> aOidcToken = authorization.getToken(OidcIdToken.class);
        if (aOidcToken != null) {
            final var aOidcTokenEntity = new OidcIdTokenEntity();
            aOidcTokenEntity.setId(IdentifierUtils.generateNewId());
            aOidcTokenEntity.setValue(aOidcToken.getToken().getTokenValue());
            aOidcTokenEntity.setIssuedAt(aOidcToken.getToken().getIssuedAt());
            aOidcTokenEntity.setExpiresAt(aOidcToken.getToken().getExpiresAt());
            aOidcTokenEntity.setMetadata(writeString(aOidcToken.getMetadata()));
            aOidcTokenEntity.setClaims(writeString(aOidcToken.getClaims()));
            aOidcTokenEntity.setAuthorization(aAuthEntity);
            aAuthEntity.setOidcIdToken(aOidcTokenEntity);
        }

        final OAuth2Authorization.Token<OAuth2UserCode> aUserCode = authorization.getToken(OAuth2UserCode.class);
        if (aUserCode != null) {
            final var aUserCodeEntity = new UserCodeEntity();
            aUserCodeEntity.setId(IdentifierUtils.generateNewId());
            aUserCodeEntity.setValue(aUserCode.getToken().getTokenValue());
            aUserCodeEntity.setIssuedAt(aUserCode.getToken().getIssuedAt());
            aUserCodeEntity.setExpiresAt(aUserCode.getToken().getExpiresAt());
            aUserCodeEntity.setMetadata(writeString(aUserCode.getMetadata()));
            aUserCodeEntity.setAuthorization(aAuthEntity);
            aAuthEntity.setUserCode(aUserCodeEntity);
        }

        final OAuth2Authorization.Token<OAuth2DeviceCode> aDeviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (aDeviceCode != null) {
            final var aDeviceCodeEntity = new DeviceCodeEntity();
            aDeviceCodeEntity.setId(IdentifierUtils.generateNewId());
            aDeviceCodeEntity.setValue(aDeviceCode.getToken().getTokenValue());
            aDeviceCodeEntity.setIssuedAt(aDeviceCode.getToken().getIssuedAt());
            aDeviceCodeEntity.setExpiresAt(aDeviceCode.getToken().getExpiresAt());
            aDeviceCodeEntity.setMetadata(writeString(aDeviceCode.getMetadata()));
            aDeviceCodeEntity.setAuthorization(aAuthEntity);
            aAuthEntity.setDeviceCode(aDeviceCodeEntity);
        }

        this.authorizationRepository.save(aAuthEntity);
    }

    @Transactional
    @Override
    public void remove(final OAuth2Authorization authorization) {
        this.authorizationRepository.deleteById(authorization.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public OAuth2Authorization findById(final String id) {
        return this.authorizationRepository.findById(id)
                .map(this::toObject)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public OAuth2Authorization findByToken(final String token, final OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return this.authorizationRepository
                    .findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(token).map(this::toObject).orElse(null);
        }

        Optional<AuthorizationEntity> result = switch (tokenType.getValue()) {
            case OAuth2ParameterNames.STATE -> this.authorizationRepository.findByState(token);
            case OAuth2ParameterNames.CODE -> this.authorizationRepository.findByAuthorizationCodeValue(token);
            case OAuth2ParameterNames.ACCESS_TOKEN -> this.authorizationRepository.findByAccessTokenValue(token);
            case OAuth2ParameterNames.REFRESH_TOKEN -> this.authorizationRepository.findByRefreshTokenValue(token);
            case OidcParameterNames.ID_TOKEN -> this.authorizationRepository.findByOidcIdTokenValue(token);
            case OAuth2ParameterNames.USER_CODE -> this.authorizationRepository.findByUserCodeValue(token);
            case OAuth2ParameterNames.DEVICE_CODE -> this.authorizationRepository.findByDeviceCodeValue(token);
            default -> Optional.empty();
        };

        return result.map(this::toObject).orElse(null);
    }

    private Map<String, Object> parseMap(final String data) {
        try {
            return this.mapper.readValue(data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeString(final Map<String, Object> data) {
        try {
            return this.mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private OAuth2Authorization toObject(AuthorizationEntity entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(StringUtils.commaDelimitedListToSet(entity.getAuthorizedScopes()))
                .attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCode() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCode().getValue(),
                    entity.getAuthorizationCode().getIssuedAt(),
                    entity.getAuthorizationCode().getExpiresAt());
            builder.token(authorizationCode, metadata -> metadata.putAll(parseMap(entity.getAuthorizationCode().getMetadata())));
        }

        if (entity.getAccessToken() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessToken().getValue(),
                    entity.getAccessToken().getIssuedAt(),
                    entity.getAccessToken().getExpiresAt(),
                    StringUtils.commaDelimitedListToSet(entity.getAccessToken().getScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessToken().getMetadata())));
        }

        if (entity.getRefreshToken() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshToken().getValue(),
                    entity.getRefreshToken().getIssuedAt(),
                    entity.getRefreshToken().getExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshToken().getMetadata())));
        }

        if (entity.getOidcIdToken() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    entity.getOidcIdToken().getValue(),
                    entity.getOidcIdToken().getIssuedAt(),
                    entity.getOidcIdToken().getExpiresAt(),
                    parseMap(entity.getOidcIdToken().getClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(entity.getOidcIdToken().getMetadata())));
        }

        if (entity.getUserCode() != null) {
            OAuth2UserCode userCode = new OAuth2UserCode(
                    entity.getUserCode().getValue(),
                    entity.getUserCode().getIssuedAt(),
                    entity.getUserCode().getExpiresAt());
            builder.token(userCode, metadata -> metadata.putAll(parseMap(entity.getUserCode().getMetadata())));
        }

        if (entity.getDeviceCode() != null) {
            OAuth2DeviceCode deviceCode = new OAuth2DeviceCode(
                    entity.getDeviceCode().getValue(),
                    entity.getDeviceCode().getIssuedAt(),
                    entity.getDeviceCode().getExpiresAt());
            builder.token(deviceCode, metadata -> metadata.putAll(parseMap(entity.getDeviceCode().getMetadata())));
        }

        return builder.build();
    }
}