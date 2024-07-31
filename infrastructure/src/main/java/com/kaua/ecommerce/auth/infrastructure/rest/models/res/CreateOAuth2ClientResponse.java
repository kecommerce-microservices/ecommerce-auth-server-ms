package com.kaua.ecommerce.auth.infrastructure.rest.models.res;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOAuth2ClientResponse(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("id") String id
) {
}
