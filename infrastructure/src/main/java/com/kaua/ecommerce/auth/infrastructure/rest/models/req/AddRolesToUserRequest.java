package com.kaua.ecommerce.auth.infrastructure.rest.models.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record AddRolesToUserRequest(
        @JsonProperty("roles_ids") Set<String> rolesIds
) {
}
