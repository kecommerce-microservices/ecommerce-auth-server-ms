package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfirmUserMfaDeviceRequest(
        @JsonProperty("valid_until") String validUntil
) {
}
