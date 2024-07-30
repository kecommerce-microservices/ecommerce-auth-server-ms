package com.kaua.ecommerce.auth.infrastructure.services.impl;

import com.kaua.ecommerce.auth.infrastructure.IntegrationTest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest.CreateOAuth2ClientSettingsRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest.CreateOAuth2ClientTokenRequest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class OAuth2ClientServiceTest {

    @Autowired
    private OAuth2ClientService oAuth2ClientService;

    @Test
    void givenAValidCreateOAuth2ClientRequest_whenSaveClient_thenShouldSaveClient() {
        final var aRequest = new CreateOAuth2ClientRequest(
                "clientId",
                "clientsecret",
                List.of("client_secret_basic"),
                List.of("client_credentials", "password"),
                List.of("http://localhost:8080"),
                List.of("openid", "profile", "email"),
                new CreateOAuth2ClientSettingsRequest(
                        false,
                        false
                ),
                new CreateOAuth2ClientTokenRequest(
                        60,
                        15,
                        false
                )
        );

        final var aSavedClient = this.oAuth2ClientService.saveClient(aRequest);

        Assertions.assertNotNull(aSavedClient);
        Assertions.assertNotNull(aSavedClient.getId());
        Assertions.assertEquals(aRequest.clientId(), aSavedClient.getClientId());
        Assertions.assertNotNull(aSavedClient.getSecret());
        Assertions.assertEquals(aRequest.authMethods().size(), aSavedClient.getAuthenticationMethods().size());
        Assertions.assertEquals(aRequest.grantTypes().size(), aSavedClient.getGrantTypes().size());
        Assertions.assertEquals(aRequest.redirectUris().size(), aSavedClient.getRedirectUrls().size());
        Assertions.assertEquals(aRequest.scopes().size(), aSavedClient.getScopes().size());
        Assertions.assertEquals(aRequest.clientSettings().requireAuthorizationConsent(), aSavedClient.getClientSettings().isRequireAuthorizationConsent());
        Assertions.assertEquals(aRequest.clientSettings().requireProofKey(), aSavedClient.getClientSettings().isRequireProofKey());
        Assertions.assertEquals(aRequest.tokenSettings().accessTokenValidityMinutes(), aSavedClient.getClientTokenSettings().getAccessTokenTTL());
        Assertions.assertEquals(aRequest.tokenSettings().refreshTokenValidityDays(), aSavedClient.getClientTokenSettings().getRefreshTokenTTL());
        Assertions.assertEquals(aRequest.tokenSettings().reuseRefreshTokens(), aSavedClient.getClientTokenSettings().isReuseRefreshTokens());
    }

    @Test
    void givenAValidExistsClientId_whenSaveClient_thenShouldThrowDomainException() {
        final var aRequest = new CreateOAuth2ClientRequest(
                "clientId",
                "clientsecret",
                List.of("client_secret_basic"),
                List.of("client_credentials", "password"),
                List.of("http://localhost:8080"),
                List.of("openid", "profile", "email"),
                new CreateOAuth2ClientSettingsRequest(
                        false,
                        false
                ),
                new CreateOAuth2ClientTokenRequest(
                        60,
                        15,
                        false
                )
        );

        final var expectedErrorMessage = "OAuth2 client with clientId already exists";

        this.oAuth2ClientService.saveClient(aRequest);

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> this.oAuth2ClientService.saveClient(aRequest)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAValidId_whenDeleteClient_thenShouldDeleteClient() {
        final var aRequest = new CreateOAuth2ClientRequest(
                "clientId",
                "clientsecret",
                List.of("client_secret_basic"),
                List.of("client_credentials", "password"),
                List.of("http://localhost:8080"),
                List.of("openid", "profile", "email"),
                new CreateOAuth2ClientSettingsRequest(
                        false,
                        false
                ),
                new CreateOAuth2ClientTokenRequest(
                        60,
                        15,
                        false
                )
        );
        this.oAuth2ClientService.saveClient(aRequest);

        final var aRequestTwo = new CreateOAuth2ClientRequest(
                "clientId2",
                "clientsecret2",
                List.of("client_secret_basic"),
                List.of("client_credentials", "password"),
                List.of("http://localhost:8080"),
                List.of("openid", "profile", "email"),
                new CreateOAuth2ClientSettingsRequest(
                        false,
                        false
                ),
                new CreateOAuth2ClientTokenRequest(
                        60,
                        15,
                        false
                )
        );

        final var aSavedClient = this.oAuth2ClientService.saveClient(aRequestTwo);

        Assertions.assertDoesNotThrow(
                () -> this.oAuth2ClientService.deleteClient(aSavedClient.getId()));
    }

    @Test
    void givenAValidIdButIsLastClient_whenDeleteClient_thenThrowsDomainException() {
        final var aRequest = new CreateOAuth2ClientRequest(
                "clientId",
                "clientsecret",
                List.of("client_secret_basic"),
                List.of("client_credentials", "password"),
                List.of("http://localhost:8080"),
                List.of("openid", "profile", "email"),
                new CreateOAuth2ClientSettingsRequest(
                        false,
                        false
                ),
                new CreateOAuth2ClientTokenRequest(
                        60,
                        15,
                        false
                )
        );

        final var aSavedClient = this.oAuth2ClientService.saveClient(aRequest);

        final var aException = Assertions.assertThrows(
                DomainException.class,
                () -> this.oAuth2ClientService.deleteClient(aSavedClient.getId())
        );

        Assertions.assertEquals("Cannot delete the last oauth2 client", aException.getMessage());
    }
}
