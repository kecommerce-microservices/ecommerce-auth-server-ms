package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserMfaRequest(
        @JsonProperty("device_name") String deviceName,
        @JsonProperty("type") String type
) {
}
