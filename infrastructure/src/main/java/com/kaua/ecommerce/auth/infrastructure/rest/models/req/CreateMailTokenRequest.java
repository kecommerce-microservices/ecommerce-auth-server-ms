package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateMailTokenRequest(
        @JsonProperty("email") String email
) {
}
