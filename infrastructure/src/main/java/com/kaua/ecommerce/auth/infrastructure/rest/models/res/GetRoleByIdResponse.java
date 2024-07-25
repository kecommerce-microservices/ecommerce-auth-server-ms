package com.kaua.ecommerce.auth.infrastructure.rest.models.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.GetRoleByIdOutput;

import java.time.Instant;

public record GetRoleByIdResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") boolean isDefault,
        @JsonProperty("is_deleted") boolean isDeleted,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt,
        @JsonProperty("version") long version
) {

    public GetRoleByIdResponse(final GetRoleByIdOutput aOutput) {
        this(
                aOutput.id(),
                aOutput.name(),
                aOutput.description(),
                aOutput.isDefault(),
                aOutput.isDeleted(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.deletedAt(),
                aOutput.version()
        );
    }
}
