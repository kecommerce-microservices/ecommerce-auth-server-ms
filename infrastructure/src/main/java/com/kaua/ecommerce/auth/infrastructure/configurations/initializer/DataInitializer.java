package com.kaua.ecommerce.auth.infrastructure.configurations.initializer;

import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.ClientRegisteredRepositoryImpl;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.*;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Component
@ConditionalOnProperty(
        value = "auth-server.data-initializer.enabled",
        havingValue = "true"
)
@Profile({"!test-integration"})
public class DataInitializer implements CommandLineRunner {

    private final ClientRegisteredRepositoryImpl clientRepository;
    private final ClientJpaEntityRepository clientJpaEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth-server.data-initializer.client.id}")
    private String clientId;

    @Value("${auth-server.data-initializer.client.secret}")
    private String clientSecret;

    @Value("${auth-server.data-initializer.client.redirect-uri}")
    private String redirectUri;

    public DataInitializer(
            final ClientRegisteredRepositoryImpl clientRepository,
            final ClientJpaEntityRepository clientJpaEntityRepository,
            final PasswordEncoder passwordEncoder
    ) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
        this.clientJpaEntityRepository = Objects.requireNonNull(clientJpaEntityRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Override
    public void run(String... args) {
        createDefaultOauthClient();

    }

    @Transactional
    private void createDefaultOauthClient() {
        if (this.clientJpaEntityRepository.count() == 0) {
            final var aClientDefault = new ClientEntity();
            aClientDefault.setId(IdentifierUtils.generateNewId());
            aClientDefault.setClientId(clientId);
            aClientDefault.setSecret(passwordEncoder.encode(clientSecret));

            final var aClientSecretBasicAuthMethod = AuthenticationMethodEntity.from(
                    ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                    aClientDefault
            );

            final var aClientJwtAuthMethod = AuthenticationMethodEntity.from(
                    ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                    aClientDefault
            );

            final var aClientAuthCodeGrantType = GrantTypeEntity.from(
                    AuthorizationGrantType.AUTHORIZATION_CODE,
                    aClientDefault
            );

            final var aClientRefreshTokenGrantType = GrantTypeEntity.from(
                    AuthorizationGrantType.REFRESH_TOKEN,
                    aClientDefault
            );

            final var aClientPasswordGrantType = GrantTypeEntity.from(
                    AuthorizationGrantType.PASSWORD,
                    aClientDefault
            );

            final var aClientClientCredentialsGrantType = GrantTypeEntity.from(
                    AuthorizationGrantType.CLIENT_CREDENTIALS,
                    aClientDefault
            );

            final var defaultRedirectUrl = RedirectUrlEntity.from(
                    redirectUri,
                    aClientDefault
            );

            final var defaultScopeOpenId = ScopeEntity.from(
                    OidcScopes.OPENID,
                    aClientDefault
            );

            final var defaultScopeProfile = ScopeEntity.from(
                    OidcScopes.PROFILE,
                    aClientDefault
            );

            final var defaultClientTokenSettings = new ClientTokenSettingsEntity(
                    IdentifierUtils.generateNewId(),
                    10,
                    1,
                    false,
                    aClientDefault
            );

            final var defaultClientSettings = new ClientSettingsEntity(
                    IdentifierUtils.generateNewId(),
                    false,
                    true,
                    aClientDefault
            );

            aClientDefault.setAuthenticationMethods(Set.of(
                    aClientSecretBasicAuthMethod,
                    aClientJwtAuthMethod
            ));
            aClientDefault.setGrantTypes(Set.of(
                    aClientAuthCodeGrantType,
                    aClientRefreshTokenGrantType,
                    aClientPasswordGrantType,
                    aClientClientCredentialsGrantType
            ));
            aClientDefault.setRedirectUrls(Set.of(defaultRedirectUrl));
            aClientDefault.setScopes(Set.of(
                    defaultScopeOpenId,
                    defaultScopeProfile
            ));
            aClientDefault.setClientTokenSettings(defaultClientTokenSettings);
            aClientDefault.setClientSettings(defaultClientSettings);

            this.clientRepository.save(ClientEntity.fromClient(aClientDefault));
        }
    }
}
