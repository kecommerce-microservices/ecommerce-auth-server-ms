package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRoleRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") boolean isDefault
) {
}
