package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR_URI;

public class CustomPasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private static final OAuth2TokenType ID_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public CustomPasswordGrantAuthenticationProvider(
            final AuthenticationManager authenticationManager,
            final OAuth2AuthorizationService authorizationService,
            final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator
    ) {
        this.authenticationManager = Objects.requireNonNull(authenticationManager);
        this.authorizationService = Objects.requireNonNull(authorizationService);
        this.tokenGenerator = Objects.requireNonNull(tokenGenerator);
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final var aPasswordGrantAuthentication = (CustomPasswordGrantAuthenticationToken) authentication;

        final var aClientPrincipal = getAuthenticatedClientElseThrow(authentication);
        final var aRegisteredClient = aClientPrincipal.getRegisteredClient();

        if (aRegisteredClient == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        if (!aRegisteredClient.getAuthorizationGrantTypes().contains(CustomPasswordGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        final var aUsernamePasswordAuth = getUsernamePasswordAuthentication(aPasswordGrantAuthentication);

        // generate access token
        final var tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(aRegisteredClient)
                .principal(aUsernamePasswordAuth)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizationGrantType(CustomPasswordGrantType.PASSWORD)
                .authorizationGrant(aPasswordGrantAuthentication);

        var aTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        final var aGeneratedAccessToken = tokenGenerator.generate(aTokenContext);

        if (aGeneratedAccessToken == null) {
            final var aError = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(aError);
        }

        final var aAccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                aGeneratedAccessToken.getTokenValue(), aGeneratedAccessToken.getIssuedAt(),
                aGeneratedAccessToken.getExpiresAt(), null);

        // Initialize the OAuth2Authorization
        final var aAuthorizationBuilder = OAuth2Authorization.withRegisteredClient(aRegisteredClient)
                .principalName(aUsernamePasswordAuth.getName())
                .authorizationGrantType(CustomPasswordGrantType.PASSWORD)
                .attribute(Principal.class.getName(), aUsernamePasswordAuth);

        if (aGeneratedAccessToken instanceof ClaimAccessor) {
            aAuthorizationBuilder.token(aAccessToken, (metadata) ->
                    metadata.put(
                            OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                            ((ClaimAccessor) aGeneratedAccessToken).getClaims()
                    ));
        } else {
            aAuthorizationBuilder.accessToken(aAccessToken);
        }

        // Generate Refresh Token
        OAuth2RefreshToken aRefreshToken = null;
        if (aRegisteredClient.getAuthorizationGrantTypes().contains(CustomPasswordGrantType.PASSWORD)) {
            aTokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            final var aGeneratedRefreshToken = this.tokenGenerator.generate(aTokenContext);
            aRefreshToken = (OAuth2RefreshToken) aGeneratedRefreshToken;
            aAuthorizationBuilder.refreshToken(aRefreshToken);
        }

        // Generate ID Token
        OidcIdToken idToken;
        aTokenContext = tokenContextBuilder
                .tokenType(ID_TOKEN_TYPE)
                .authorization(aAuthorizationBuilder.build())
                .build();

        OAuth2Token aGeneratedIdToken = this.tokenGenerator.generate(aTokenContext);
        if (!(aGeneratedIdToken instanceof Jwt)) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the ID token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        idToken = new OidcIdToken((aGeneratedIdToken.getTokenValue()), aGeneratedIdToken.getIssuedAt(),
                aGeneratedIdToken.getExpiresAt(), ((Jwt) aGeneratedIdToken).getClaims());

        aAuthorizationBuilder.token(idToken, (metadata) ->
                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));

        OAuth2Authorization authorization = aAuthorizationBuilder.build();

        this.authorizationService.save(authorization);

        Map<String, Object> aAdditionalParameters = new HashMap<>();
        aAdditionalParameters.put("scope", "openid");
        aAdditionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());

        return new OAuth2AccessTokenAuthenticationToken(
                aRegisteredClient,
                aClientPrincipal,
                aAccessToken,
                aRefreshToken,
                aAdditionalParameters
        );
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return CustomPasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication getUsernamePasswordAuthentication(
            final CustomPasswordGrantAuthenticationToken resourceOwnerPasswordAuthentication
    ) {

        Map<String, Object> additionalParameters = resourceOwnerPasswordAuthentication.getAdditionalParameters();

        String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
        String password = (String) additionalParameters.get(OAuth2ParameterNames.PASSWORD);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrow(
            final Authentication authentication
    ) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;

        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }

        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}
