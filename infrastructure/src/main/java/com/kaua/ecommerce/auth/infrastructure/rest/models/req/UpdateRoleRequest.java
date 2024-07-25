package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateRoleRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") Boolean isDefault
) {
}
