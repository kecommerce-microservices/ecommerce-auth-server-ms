package com.kaua.ecommerce.auth.infrastructure.rest.models.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.GetUserByIdOutput;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record GetUserByIdResponse(
        @JsonProperty("user_id") String userId,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("email") String email,
        @JsonProperty("roles") Set<GetUserByIdResponse.GetUserByIdRolesResponse> roles,
        @JsonProperty("is_deleted") boolean isDeleted,
        @JsonProperty("email_verified") boolean emailVerified,
        @JsonProperty("mfa") GetUserByIdResponse.GetUserByIdMfaResponse mfa,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt,
        @JsonProperty("version") long version
) {

    public GetUserByIdResponse(
            final GetUserByIdOutput aOutput
    ) {
        this(
                aOutput.userId(),
                aOutput.customerId(),
                aOutput.firstName(),
                aOutput.lastName(),
                aOutput.fullName(),
                aOutput.email(),
                aOutput.roles().stream()
                        .map(it -> new GetUserByIdResponse
                                .GetUserByIdRolesResponse(it.roleId(), it.roleName()))
                        .collect(Collectors.toSet()),
                aOutput.isDeleted(),
                aOutput.emailVerified(),
                new GetUserByIdResponse.GetUserByIdMfaResponse(aOutput.mfa()),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.deletedAt(),
                aOutput.version()
        );
    }

    public record GetUserByIdRolesResponse(
            @JsonProperty("role_id") String roleId,
            @JsonProperty("role_name") String roleName
    ) {
    }

    public record GetUserByIdMfaResponse(
            @JsonProperty("mfa_id") String mfaId,
            @JsonProperty("mfa_enabled") boolean mfaEnabled,
            @JsonProperty("mfa_verified") boolean mfaVerified,
            @JsonProperty("device_name") String deviceName,
            @JsonProperty("device_verified") boolean deviceVerified,
            @JsonProperty("mfa_type") String mfaType,
            @JsonProperty("valid_until") Instant validUntil
    ) {

        public GetUserByIdMfaResponse(final GetUserByIdOutput.GetUserByIdMfaOutput aMfa) {
            this(
                    aMfa.mfaId(),
                    aMfa.mfaEnabled(),
                    aMfa.mfaVerified(),
                    aMfa.deviceName(),
                    aMfa.deviceVerified(),
                    aMfa.mfaType(),
                    aMfa.validUntil()
            );
        }
    }
}
