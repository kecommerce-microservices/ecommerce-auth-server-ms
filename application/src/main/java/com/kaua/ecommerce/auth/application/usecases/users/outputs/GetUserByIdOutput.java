package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfa;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;

import java.time.Instant;
import java.util.Set;

public record GetUserByIdOutput(
        String userId,
        String customerId,
        String firstName,
        String lastName,
        String fullName,
        String email,
        Set<GetUserByIdRolesOutput> roles,
        boolean isDeleted,
        boolean emailVerified,
        GetUserByIdMfaOutput mfa,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        long version
) {

    public GetUserByIdOutput(
            final User aUser,
            Set<GetUserByIdRolesOutput> aRoles
    ) {
        this(
                aUser.getId().value().toString(),
                aUser.getCustomerId().value().toString(),
                aUser.getName().firstName(),
                aUser.getName().lastName(),
                aUser.getName().fullName(),
                aUser.getEmail().value(),
                aRoles,
                aUser.isDeleted(),
                aUser.isEmailVerified(),
                new GetUserByIdMfaOutput(aUser.getMfa()),
                aUser.getCreatedAt(),
                aUser.getUpdatedAt(),
                aUser.getDeletedAt().orElse(null),
                aUser.getVersion()
        );
    }

    public record GetUserByIdRolesOutput(
            String roleId,
            String roleName
    ) {
    }

    public record GetUserByIdMfaOutput(
            String mfaId,
            boolean mfaEnabled,
            boolean mfaVerified,
            String deviceName,
            boolean deviceVerified,
            String mfaType,
            Instant validUntil
    ) {

        public GetUserByIdMfaOutput(final UserMfa aMfa) {
            this(
                    aMfa.getId().value().toString(),
                    aMfa.isMfaEnabled(),
                    aMfa.isMfaVerified(),
                    aMfa.getDeviceName().orElse(null),
                    aMfa.isDeviceVerified(),
                    aMfa.getMfaType().map(UserMfaType::name).orElse(null),
                    aMfa.getValidUntil().orElse(null)
            );
        }
    }
}
