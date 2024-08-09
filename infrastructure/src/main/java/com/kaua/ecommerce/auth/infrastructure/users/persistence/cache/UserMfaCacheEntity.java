package com.kaua.ecommerce.auth.infrastructure.users.persistence.cache;

import com.kaua.ecommerce.auth.domain.users.mfas.UserMfa;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserMfaJpaEntity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UserMfaCacheEntity implements Serializable {

    private UUID id;
    private boolean mfaEnabled;
    private boolean mfaVerified;
    private String mfaSecret;
    private String deviceName;
    private boolean deviceVerified;
    private String mfaType;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant validUntil;

    public UserMfaCacheEntity() {
    }

    public UserMfaCacheEntity(
            final UUID id,
            final boolean mfaEnabled,
            final boolean mfaVerified,
            final String mfaSecret,
            final String deviceName,
            final boolean deviceVerified,
            final String mfaType,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant validUntil
    ) {
        this.id = id;
        this.mfaEnabled = mfaEnabled;
        this.mfaVerified = mfaVerified;
        this.mfaSecret = mfaSecret;
        this.deviceName = deviceName;
        this.deviceVerified = deviceVerified;
        this.mfaType = mfaType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.validUntil = validUntil;
    }

    public static UserMfaCacheEntity toEntity(final UserMfaJpaEntity aEntity) {
        return new UserMfaCacheEntity(
                aEntity.getId(),
                aEntity.isMfaEnabled(),
                aEntity.isMfaVerified(),
                aEntity.getMfaSecret().orElse(null),
                aEntity.getDeviceName().orElse(null),
                aEntity.isDeviceVerified(),
                aEntity.getMfaType().map(UserMfaType::name).orElse(null),
                aEntity.getCreatedAt(),
                aEntity.getUpdatedAt(),
                aEntity.getValidUntil().orElse(null)
        );
    }

    public UserMfa toDomain() {
        return UserMfa.with(
                new UserMfaId(getId()),
                0,
                isMfaEnabled(),
                isMfaVerified(),
                getMfaSecret().orElse(null),
                getDeviceName().orElse(null),
                isDeviceVerified(),
                getMfaType().map(UserMfaType::valueOf).orElse(null),
                getCreatedAt(),
                getUpdatedAt(),
                getValidUntil().orElse(null)
        );
    }

    public UUID getId() {
        return id;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public boolean isMfaVerified() {
        return mfaVerified;
    }

    public Optional<String> getMfaSecret() {
        return Optional.ofNullable(mfaSecret);
    }

    public Optional<String> getDeviceName() {
        return Optional.ofNullable(deviceName);
    }

    public boolean isDeviceVerified() {
        return deviceVerified;
    }

    public Optional<String> getMfaType() {
        return Optional.ofNullable(mfaType);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<Instant> getValidUntil() {
        return Optional.ofNullable(validUntil);
    }
}
