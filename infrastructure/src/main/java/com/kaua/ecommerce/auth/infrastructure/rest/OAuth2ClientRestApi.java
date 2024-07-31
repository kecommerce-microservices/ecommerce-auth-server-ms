package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateOAuth2ClientRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.CreateOAuth2ClientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth2 Clients", description = "OAuth2 Clients API")
@RequestMapping(value = "/v1/oauth2-clients")
public interface OAuth2ClientRestApi {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create OAuth2 Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OAuth2 Client created"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ResponseEntity<CreateOAuth2ClientResponse> createOauth2Client(@RequestBody CreateOAuth2ClientRequest request);

    @DeleteMapping(value = "/{clientId}")
    @Operation(summary = "Delete OAuth2 Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OAuth2 Client deleted"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteClient(@PathVariable String clientId);
}
