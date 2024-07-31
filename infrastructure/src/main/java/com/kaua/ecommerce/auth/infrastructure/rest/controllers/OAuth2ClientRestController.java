package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.infrastructure.rest.OAuth2ClientRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.CreateOAuth2ClientResponse;
import com.kaua.ecommerce.auth.infrastructure.services.impl.OAuth2ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class OAuth2ClientRestController implements OAuth2ClientRestApi {

    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientRestController.class);

    private final OAuth2ClientService oAuth2ClientService;

    public OAuth2ClientRestController(
            final OAuth2ClientService oAuth2ClientService
    ) {
        this.oAuth2ClientService = Objects.requireNonNull(oAuth2ClientService);
    }

    @Override
    public ResponseEntity<CreateOAuth2ClientResponse> createOauth2Client(final CreateOAuth2ClientRequest request) {
        log.debug("Creating oauth2 client with request: {}", request);

        final var aClient = this.oAuth2ClientService.saveClient(request);

        log.info("Created oauth2 client with id: {} and clientId {}", aClient.getId(), aClient.getClientId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateOAuth2ClientResponse(aClient.getId(), aClient.getClientId()));
    }

    @Override
    public void deleteClient(final String clientId) {
        log.debug("Deleting oauth2 client with id: {}", clientId);
        this.oAuth2ClientService.deleteClient(clientId);
        log.info("Deleted oauth2 client with id: {}", clientId);
    }
}
