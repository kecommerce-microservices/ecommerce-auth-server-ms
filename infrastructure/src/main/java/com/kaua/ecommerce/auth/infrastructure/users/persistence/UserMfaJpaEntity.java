package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.domain.users.mfas.UserMfa;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfaType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users_mfa")
public class UserMfaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabledEntity;

    @Column(name = "mfa_verified", nullable = false)
    private boolean mfaVerifiedEntity;

    @Column(name = "mfa_secret")
    private String mfaSecretEntity;

    @Column(name = "device_name")
    private String deviceNameEntity;

    @Column(name = "device_verified", nullable = false)
    private boolean deviceVerifiedEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_type")
    private UserMfaType mfaTypeEntity;

    @Column(name = "created_at", nullable = false)
    private Instant createdAtEntity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAtEntity;

    @Column(name = "valid_until")
    private Instant validUntilEntity;

    public UserMfaJpaEntity() {
    }

    private UserMfaJpaEntity(
            final UUID id,
            final boolean mfaEnabledEntity,
            final boolean mfaVerifiedEntity,
            final String mfaSecretEntity,
            final String deviceNameEntity,
            final boolean deviceVerifiedEntity,
            final UserMfaType mfaTypeEntity,
            final Instant createdAtEntity,
            final Instant updatedAtEntity,
            final Instant validUntilEntity
    ) {
        this.id = id;
        this.mfaEnabledEntity = mfaEnabledEntity;
        this.mfaVerifiedEntity = mfaVerifiedEntity;
        this.mfaSecretEntity = mfaSecretEntity;
        this.deviceNameEntity = deviceNameEntity;
        this.deviceVerifiedEntity = deviceVerifiedEntity;
        this.mfaTypeEntity = mfaTypeEntity;
        this.createdAtEntity = createdAtEntity;
        this.updatedAtEntity = updatedAtEntity;
        this.validUntilEntity = validUntilEntity;
    }

    public static UserMfaJpaEntity toEntity(final UserMfa aMfa) {
        return new UserMfaJpaEntity(
                aMfa.getId().value(),
                aMfa.isMfaEnabled(),
                aMfa.isMfaVerified(),
                aMfa.getMfaSecret().orElse(null),
                aMfa.getDeviceName().orElse(null),
                aMfa.isDeviceVerified(),
                aMfa.getMfaType().orElse(null),
                aMfa.getCreatedAt(),
                aMfa.getUpdatedAt(),
                aMfa.getValidUntil().orElse(null)
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
                getMfaType().orElse(null),
                getCreatedAt(),
                getUpdatedAt(),
                getValidUntil().orElse(null)
        );
    }

    public UUID getId() {
        return id;
    }

    public boolean isMfaEnabled() {
        return mfaEnabledEntity;
    }

    public boolean isMfaVerified() {
        return mfaVerifiedEntity;
    }

    public Optional<String> getMfaSecret() {
        return Optional.ofNullable(mfaSecretEntity);
    }

    public Optional<String> getDeviceName() {
        return Optional.ofNullable(deviceNameEntity);
    }

    public boolean isDeviceVerified() {
        return deviceVerifiedEntity;
    }

    public Optional<UserMfaType> getMfaType() {
        return Optional.ofNullable(mfaTypeEntity);
    }

    public Instant getCreatedAt() {
        return createdAtEntity;
    }

    public Instant getUpdatedAt() {
        return updatedAtEntity;
    }

    public Optional<Instant> getValidUntil() {
        return Optional.ofNullable(validUntilEntity);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabledEntity = mfaEnabled;
    }

    public void setMfaVerified(boolean mfaVerified) {
        this.mfaVerifiedEntity = mfaVerified;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecretEntity = mfaSecret;
    }

    public void setDeviceName(String deviceName) {
        this.deviceNameEntity = deviceName;
    }

    public void setDeviceVerified(boolean deviceVerified) {
        this.deviceVerifiedEntity = deviceVerified;
    }

    public void setMfaType(UserMfaType mfaType) {
        this.mfaTypeEntity = mfaType;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAtEntity = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAtEntity = updatedAt;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntilEntity = validUntil;
    }
}
