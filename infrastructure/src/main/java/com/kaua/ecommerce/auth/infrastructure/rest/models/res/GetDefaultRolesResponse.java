package com.kaua.ecommerce.auth.infrastructure.rest.models.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetDefaultRolesOutput;

import java.time.Instant;

public record GetDefaultRolesResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") boolean isDefault,
        @JsonProperty("is_deleted") boolean isDeleted,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

    public GetDefaultRolesResponse(final GetDefaultRolesOutput aOutput) {
        this(
                aOutput.id(),
                aOutput.name(),
                aOutput.description(),
                aOutput.isDefault(),
                aOutput.isDeleted(),
                aOutput.createdAt(),
                aOutput.updatedAt()
        );
    }
}
