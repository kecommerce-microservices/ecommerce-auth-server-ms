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
    private boolean mfaEnabled;

    @Column(name = "mfa_verified", nullable = false)
    private boolean mfaVerified;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_verified", nullable = false)
    private boolean deviceVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_type")
    private UserMfaType mfaType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "valid_until")
    private Instant validUntil;

    public UserMfaJpaEntity() {
    }

    private UserMfaJpaEntity(
            final UUID id,
            final boolean mfaEnabled,
            final boolean mfaVerified,
            final String mfaSecret,
            final String deviceName,
            final boolean deviceVerified,
            final UserMfaType mfaType,
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

    public Optional<UserMfaType> getMfaType() {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public void setMfaVerified(boolean mfaVerified) {
        this.mfaVerified = mfaVerified;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceVerified(boolean deviceVerified) {
        this.deviceVerified = deviceVerified;
    }

    public void setMfaType(UserMfaType mfaType) {
        this.mfaType = mfaType;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }
}
