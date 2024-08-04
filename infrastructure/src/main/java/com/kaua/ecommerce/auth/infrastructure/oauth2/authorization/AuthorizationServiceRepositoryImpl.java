package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence.*;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
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

import java.util.*;

@Component
public class AuthorizationServiceRepositoryImpl implements OAuth2AuthorizationService {

    private final AuthorizationJpaEntityRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final UserJpaEntityRepository userJpaEntityRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthorizationServiceRepositoryImpl(
            final AuthorizationJpaEntityRepository authorizationRepository,
            final RegisteredClientRepository registeredClientRepository,
            final UserJpaEntityRepository userJpaEntityRepository
    ) {
        this.authorizationRepository = Objects.requireNonNull(authorizationRepository);
        this.registeredClientRepository = Objects.requireNonNull(registeredClientRepository);
        this.userJpaEntityRepository = Objects.requireNonNull(userJpaEntityRepository);
        ClassLoader classLoader = AuthorizationServiceRepositoryImpl.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.mapper.registerModules(securityModules);
        this.mapper.registerModules(new OAuth2AuthorizationServerJackson2Module());
        this.mapper.registerModules(new CoreJackson2Module());
    }

    // cuidado quando for usar o BFF, precisamos ver se não vai querer salvar 2x o auth_code ou alguma outra informação
    @Transactional
    @Override
    public void save(final OAuth2Authorization authorization) {
        var aAuthEntity = this.authorizationRepository
                .findById(authorization.getId())
                .orElse(null);

        if (aAuthEntity == null) {
            final var aAuthEntityCreate = new AuthorizationEntity();
            aAuthEntityCreate.setId(authorization.getId());
            aAuthEntityCreate.setRegisteredClientId(authorization.getRegisteredClientId());
            aAuthEntityCreate.setPrincipalName(authorization.getPrincipalName());

            aAuthEntityCreate.setAuthorizationGrantType(authorization.
                    getAuthorizationGrantType().getValue());

            aAuthEntityCreate.setAuthorizedScopes(StringUtils.collectionToDelimitedString(
                    authorization.getAuthorizedScopes(), ","));

            aAuthEntityCreate.setAttributes(writeString(authorization.getAttributes()));
            aAuthEntityCreate.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

            final OAuth2Authorization.Token<OAuth2AuthorizationCode> aAuthCode = authorization.getToken(OAuth2AuthorizationCode.class);
            if (aAuthCode != null) {
                final var aAuthCodeEntity = createAuthorizationCodeEntity(aAuthEntityCreate, aAuthCode);
                aAuthEntityCreate.setAuthorizationCode(aAuthCodeEntity);
            }

            final OAuth2Authorization.Token<OAuth2AccessToken> aAccessToken = authorization.getToken(OAuth2AccessToken.class);
            if (aAccessToken != null) {
                final var aAccessTokenEntity = createAccessTokenEntity(aAuthEntityCreate, aAccessToken);
                aAuthEntityCreate.setAccessToken(aAccessTokenEntity);
            }

            final OAuth2Authorization.Token<OAuth2RefreshToken> aRefreshToken = authorization.getToken(OAuth2RefreshToken.class);
            if (aRefreshToken != null) {
                final var aRefreshTokenEntity = createRefreshTokenEntity(aAuthEntityCreate, aRefreshToken);
                aAuthEntityCreate.setRefreshToken(aRefreshTokenEntity);
            }

            final OAuth2Authorization.Token<OidcIdToken> aOidcToken = authorization.getToken(OidcIdToken.class);
            if (aOidcToken != null) {
                final var aOidcTokenEntity = createOidcTokenEntity(aAuthEntityCreate, aOidcToken);
                aAuthEntityCreate.setOidcIdToken(aOidcTokenEntity);
            }

            final OAuth2Authorization.Token<OAuth2UserCode> aUserCode = authorization.getToken(OAuth2UserCode.class);
            if (aUserCode != null) {
                final var aUserCodeEntity = createUserCodeEntity(aAuthEntityCreate, aUserCode);
                aAuthEntityCreate.setUserCode(aUserCodeEntity);
            }

            final OAuth2Authorization.Token<OAuth2DeviceCode> aDeviceCode = authorization.getToken(OAuth2DeviceCode.class);
            if (aDeviceCode != null) {
                final var aDeviceCodeEntity = createDeviceCodeEntity(aAuthEntityCreate, aDeviceCode);
                aAuthEntityCreate.setDeviceCode(aDeviceCodeEntity);
            }

            this.authorizationRepository.save(aAuthEntityCreate);
            return;
        }

        this.authorizationRepository.save(update(aAuthEntity, authorization));
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

    private AuthorizationEntity update(final AuthorizationEntity entity, final OAuth2Authorization authorization) {
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());

        entity.setAuthorizationGrantType(authorization.
                getAuthorizationGrantType().getValue());

        entity.setAuthorizedScopes(StringUtils.collectionToDelimitedString(
                authorization.getAuthorizedScopes(), ","));

        entity.setAttributes(writeString(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        final OAuth2Authorization.Token<OAuth2AuthorizationCode> aAuthCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (aAuthCode != null) {
            if (entity.getAuthorizationCode() != null) {
                entity.getAuthorizationCode().setValue(aAuthCode.getToken().getTokenValue());
                entity.getAuthorizationCode().setIssuedAt(aAuthCode.getToken().getIssuedAt());
                entity.getAuthorizationCode().setExpiresAt(aAuthCode.getToken().getExpiresAt());
                entity.getAuthorizationCode().setMetadata(writeString(aAuthCode.getMetadata()));
            } else {
                final var aAuthCodeEntity = createAuthorizationCodeEntity(entity, aAuthCode);
                entity.setAuthorizationCode(aAuthCodeEntity);
            }
        }

        final OAuth2Authorization.Token<OAuth2AccessToken> aAccessToken = authorization.getToken(OAuth2AccessToken.class);
        if (aAccessToken != null) {
            if (entity.getAccessToken() != null) {
                entity.getAccessToken().setValue(aAccessToken.getToken().getTokenValue());
                entity.getAccessToken().setIssuedAt(aAccessToken.getToken().getIssuedAt());
                entity.getAccessToken().setExpiresAt(aAccessToken.getToken().getExpiresAt());
                entity.getAccessToken().setMetadata(writeString(aAccessToken.getMetadata()));
                entity.getAccessToken().setType(aAccessToken.getToken().getTokenType().getValue());
                entity.getAccessToken().setScopes(StringUtils.collectionToDelimitedString(
                        aAccessToken.getToken().getScopes(), ","));
            } else {
                final var aAccessTokenEntity = createAccessTokenEntity(entity, aAccessToken);
                entity.setAccessToken(aAccessTokenEntity);
            }
        }

        final OAuth2Authorization.Token<OAuth2RefreshToken> aRefreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (aRefreshToken != null) {
            if (entity.getRefreshToken() != null) {
                refreshValidUntilMfa(entity, aRefreshToken);
                entity.getRefreshToken().setValue(aRefreshToken.getToken().getTokenValue());
                entity.getRefreshToken().setIssuedAt(aRefreshToken.getToken().getIssuedAt());
                entity.getRefreshToken().setExpiresAt(aRefreshToken.getToken().getExpiresAt());
                entity.getRefreshToken().setMetadata(writeString(aRefreshToken.getMetadata()));
            } else {
                final var aRefreshTokenEntity = createRefreshTokenEntity(entity, aRefreshToken);
                entity.setRefreshToken(aRefreshTokenEntity);
            }
        }

        final OAuth2Authorization.Token<OidcIdToken> aOidcToken = authorization.getToken(OidcIdToken.class);
        if (aOidcToken != null) {
            if (entity.getOidcIdToken() != null) {
                entity.getOidcIdToken().setValue(aOidcToken.getToken().getTokenValue());
                entity.getOidcIdToken().setIssuedAt(aOidcToken.getToken().getIssuedAt());
                entity.getOidcIdToken().setExpiresAt(aOidcToken.getToken().getExpiresAt());
                entity.getOidcIdToken().setMetadata(writeString(aOidcToken.getMetadata()));
                entity.getOidcIdToken().setClaims(writeString(aOidcToken.getClaims()));
            } else {
                final var aOidcTokenEntity = createOidcTokenEntity(entity, aOidcToken);
                entity.setOidcIdToken(aOidcTokenEntity);
            }
        }

        final OAuth2Authorization.Token<OAuth2UserCode> aUserCode = authorization.getToken(OAuth2UserCode.class);
        if (aUserCode != null) {
            if (entity.getUserCode() != null) {
                entity.getUserCode().setValue(aUserCode.getToken().getTokenValue());
                entity.getUserCode().setIssuedAt(aUserCode.getToken().getIssuedAt());
                entity.getUserCode().setExpiresAt(aUserCode.getToken().getExpiresAt());
                entity.getUserCode().setMetadata(writeString(aUserCode.getMetadata()));
            } else {
                final var aUserCodeEntity = createUserCodeEntity(entity, aUserCode);
                entity.setUserCode(aUserCodeEntity);
            }
        }

        final OAuth2Authorization.Token<OAuth2DeviceCode> aDeviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (aDeviceCode != null) {
            if (entity.getDeviceCode() != null) {
                entity.getDeviceCode().setValue(aDeviceCode.getToken().getTokenValue());
                entity.getDeviceCode().setIssuedAt(aDeviceCode.getToken().getIssuedAt());
                entity.getDeviceCode().setExpiresAt(aDeviceCode.getToken().getExpiresAt());
                entity.getDeviceCode().setMetadata(writeString(aDeviceCode.getMetadata()));
            } else {
                final var aDeviceCodeEntity = createDeviceCodeEntity(entity, aDeviceCode);
                entity.setDeviceCode(aDeviceCodeEntity);
            }
        }

        return entity;
    }

    private AuthorizationCodeEntity createAuthorizationCodeEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2AuthorizationCode> aAuthCode) {
        return new AuthorizationCodeEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aAuthCode.getToken().getTokenValue(),
                aAuthCode.getToken().getIssuedAt(),
                aAuthCode.getToken().getExpiresAt(),
                writeString(aAuthCode.getMetadata())
        );
    }

    private AccessTokenEntity createAccessTokenEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2AccessToken> aAccessToken) {
        return new AccessTokenEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aAccessToken.getToken().getTokenValue(),
                aAccessToken.getToken().getIssuedAt(),
                aAccessToken.getToken().getExpiresAt(),
                writeString(aAccessToken.getMetadata()),
                aAccessToken.getToken().getTokenType().getValue(),
                aAccessToken.getToken().getScopes() != null ? StringUtils.collectionToDelimitedString(
                        aAccessToken.getToken().getScopes(), ",") : null
        );
    }

    private RefreshTokenEntity createRefreshTokenEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2RefreshToken> aRefreshToken) {
        refreshValidUntilMfa(entity, aRefreshToken);
        return new RefreshTokenEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aRefreshToken.getToken().getTokenValue(),
                aRefreshToken.getToken().getIssuedAt(),
                aRefreshToken.getToken().getExpiresAt(),
                writeString(aRefreshToken.getMetadata())
        );
    }

    private void refreshValidUntilMfa(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2RefreshToken> aRefreshToken) {
        final var aUser = this.userJpaEntityRepository
                .findById(UUID.fromString(entity.getPrincipalName())).get()
                .toDomain();

        if (aUser.getMfa().isMfaEnabled()) {
            aUser.getMfa().updateValidUntil(aRefreshToken.getToken().getExpiresAt());

            this.userJpaEntityRepository.save(UserJpaEntity.toEntity(aUser));
        }
    }

    private OidcIdTokenEntity createOidcTokenEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OidcIdToken> aOidcToken) {
        return new OidcIdTokenEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aOidcToken.getToken().getTokenValue(),
                aOidcToken.getToken().getIssuedAt(),
                aOidcToken.getToken().getExpiresAt(),
                writeString(aOidcToken.getMetadata()),
                writeString(aOidcToken.getClaims())
        );
    }

    private UserCodeEntity createUserCodeEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2UserCode> aUserCode) {
        return new UserCodeEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aUserCode.getToken().getTokenValue(),
                aUserCode.getToken().getIssuedAt(),
                aUserCode.getToken().getExpiresAt(),
                writeString(aUserCode.getMetadata())
        );
    }

    private DeviceCodeEntity createDeviceCodeEntity(AuthorizationEntity entity, OAuth2Authorization.Token<OAuth2DeviceCode> aDeviceCode) {
        return new DeviceCodeEntity(
                IdentifierUtils.generateNewId(),
                entity,
                aDeviceCode.getToken().getTokenValue(),
                aDeviceCode.getToken().getIssuedAt(),
                aDeviceCode.getToken().getExpiresAt(),
                writeString(aDeviceCode.getMetadata())
        );
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
}