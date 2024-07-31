package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.infrastructure.ApiTest;
import com.kaua.ecommerce.auth.infrastructure.ControllerTest;
import com.kaua.ecommerce.auth.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.auth.infrastructure.rest.controllers.OAuth2ClientRestController;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest.CreateOAuth2ClientSettingsRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest.CreateOAuth2ClientTokenRequest;
import com.kaua.ecommerce.auth.infrastructure.services.impl.OAuth2ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = OAuth2ClientRestController.class)
class OAuth2ClientRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OAuth2ClientService oAuth2ClientService;

    @Captor
    private ArgumentCaptor<CreateOAuth2ClientRequest> createClientRequestCaptor;

    @Test
    void givenAValidRequest_whenCallCreateClient_thenReturnClientIdAndId() throws Exception {
        final var aClientId = "teste";
        final var aClientSecret = "123456Ab*";
        final var aAuthMethods = List.of("client_secret_basic", "client_secret_jwt");
        final var aGrantTypes = List.of("authorization_code", "refresh_token");
        final var aRedirectUris = List.of("http://localhost:8080");
        final var aScopes = List.of("read", "write");
        final var aAccessTokenValidity = 60;
        final var aRefreshTokenValidity = 10;
        final var aRequireProofKey = true;
        final var aRequireUserConsent = true;
        final var aReuseRefreshTokens = true;

        final var aInput = new CreateOAuth2ClientRequest(
                aClientId,
                aClientSecret,
                aAuthMethods,
                aGrantTypes,
                aRedirectUris,
                aScopes,
                new CreateOAuth2ClientSettingsRequest(
                        aRequireUserConsent,
                        aRequireProofKey
                ),
                new CreateOAuth2ClientTokenRequest(
                        aAccessTokenValidity,
                        aRefreshTokenValidity,
                        aReuseRefreshTokens
                )
        );

        Mockito.when(oAuth2ClientService.saveClient(any()))
                .thenAnswer(call -> aInput.toEntity(aClientSecret));

        final var aRequest = MockMvcRequestBuilders.post("/v1/oauth2-clients")
                .with(ApiTest.TEST_ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Json.writeValueAsString(aInput));

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.client_id").value(aClientId));

        Mockito.verify(oAuth2ClientService, Mockito.times(1))
                .saveClient(createClientRequestCaptor.capture());

        final var aCreateOAuth2ClientInput = createClientRequestCaptor.getValue();

        Assertions.assertEquals(aClientId, aCreateOAuth2ClientInput.clientId());
        Assertions.assertEquals(aClientSecret, aCreateOAuth2ClientInput.clientSecret());
        Assertions.assertEquals(aAuthMethods, aCreateOAuth2ClientInput.authMethods());
        Assertions.assertEquals(aGrantTypes, aCreateOAuth2ClientInput.grantTypes());
        Assertions.assertEquals(aRedirectUris, aCreateOAuth2ClientInput.redirectUris());
        Assertions.assertEquals(aScopes, aCreateOAuth2ClientInput.scopes());
        Assertions.assertEquals(aRequireUserConsent, aCreateOAuth2ClientInput.clientSettings().requireAuthorizationConsent());
        Assertions.assertEquals(aRequireProofKey, aCreateOAuth2ClientInput.clientSettings().requireProofKey());
        Assertions.assertEquals(aAccessTokenValidity, aCreateOAuth2ClientInput.tokenSettings().accessTokenValidityMinutes());
        Assertions.assertEquals(aRefreshTokenValidity, aCreateOAuth2ClientInput.tokenSettings().refreshTokenValidityDays());
        Assertions.assertEquals(aReuseRefreshTokens, aCreateOAuth2ClientInput.tokenSettings().reuseRefreshTokens());
    }

    @Test
    void givenAValidId_whenCallDeleteClient_thenReturnNoContent() throws Exception {
        final var aClientId = UUID.randomUUID().toString();

        final var aRequest = MockMvcRequestBuilders.delete("/v1/oauth2-clients/" + aClientId)
                .with(ApiTest.TEST_ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());

        Mockito.verify(oAuth2ClientService, Mockito.times(1))
                .deleteClient(aClientId);
    }
}
