package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangeUserPasswordRequest(
        @JsonProperty("password") String password
) {
}
