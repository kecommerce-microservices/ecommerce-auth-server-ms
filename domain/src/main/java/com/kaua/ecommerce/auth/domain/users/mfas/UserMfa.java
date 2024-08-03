package com.kaua.ecommerce.auth.domain.users.mfas;

import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class UserMfa extends AggregateRoot<UserMfaId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private boolean mfaEnabled; // se a autenticação de dois fatores está habilitada
    private boolean mfaVerified; // se o código de verificação foi validado
    private String mfaSecret; // chave secreta usada para gerar o código de verificação
    private String deviceName; // nome do dispositivo
    private boolean deviceVerified; // se o dispositivo foi verificado
    private UserMfaType mfaType; // tipo de autenticação de dois fatores
    private Instant createdAt;
    private Instant updatedAt;
    private Instant validUntil; // valido até

    private UserMfa(
            final UserMfaId aUserMfaId,
            final long aVersion,
            final boolean aMfaEnabled,
            final boolean aMfaVerified,
            final String aMfaSecret,
            final String aDeviceName,
            final boolean aDeviceVerified,
            final UserMfaType aMfaType,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aValidUntil
    ) {
        super(aUserMfaId, aVersion);
        this.setMfaEnabled(aMfaEnabled);
        this.setMfaVerified(aMfaVerified);
        this.setMfaSecret(aMfaSecret);
        this.setDeviceName(aDeviceName);
        this.setDeviceVerified(aDeviceVerified);
        this.setMfaType(aMfaType);
        this.setCreatedAt(aCreatedAt);
        this.setUpdatedAt(aUpdatedAt);
        this.setValidUntil(aValidUntil);
    }

    public static UserMfa newMfa() {
        final var aId = new UserMfaId(IdentifierUtils.generateNewUUID());
        final var aNow = InstantUtils.now();

        return new UserMfa(
                aId,
                0,
                false,
                false,
                null,
                null,
                false,
                null,
                aNow,
                aNow,
                null
        );
    }

    public UserMfa createMfaOnDevice(
            final String aMfaSecret,
            final String aDeviceName,
            final UserMfaType aMfaType
    ) {
        this.setMfaEnabled(true);
        this.setMfaSecret(aMfaSecret);
        this.setDeviceName(aDeviceName);
        this.setMfaType(aMfaType);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public UserMfa confirmDevice(final Instant aValidUntil) {
        this.setDeviceVerified(true);
        this.setValidUntil(aValidUntil);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public UserMfa updateValidUntil(final Instant aValidUntil) {
        this.setValidUntil(aValidUntil);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public boolean isValid() {
        return this.getValidUntil().map(it -> InstantUtils.now().isBefore(it))
                .orElse(false) && this.isMfaVerified() && this.isDeviceVerified();
    }

    public UserMfa verifyMfa() {
        this.setMfaVerified(true);
        this.setValidUntil(InstantUtils.now().plus(30, ChronoUnit.MINUTES));
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public UserMfa disableMfa() {
        this.setMfaEnabled(false);
        this.setMfaVerified(false);
        this.setMfaSecret(null);
        this.setDeviceName(null);
        this.setDeviceVerified(false);
        this.setMfaType(null);
        this.setValidUntil(null);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public static UserMfa with(
            final UserMfaId aUserMfaId,
            final long aVersion,
            final boolean aMfaEnabled,
            final boolean aMfaVerified,
            final String aMfaSecret,
            final String aDeviceName,
            final boolean aDeviceVerified,
            final UserMfaType aMfaType,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aValidUntil
    ) {
        return new UserMfa(
                aUserMfaId,
                aVersion,
                aMfaEnabled,
                aMfaVerified,
                aMfaSecret,
                aDeviceName,
                aDeviceVerified,
                aMfaType,
                aCreatedAt,
                aUpdatedAt,
                aValidUntil
        );
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

    private void setMfaEnabled(final boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    private void setMfaVerified(final boolean mfaVerified) {
        this.mfaVerified = mfaVerified;
    }

    private void setMfaSecret(final String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    private void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    private void setDeviceVerified(final boolean deviceVerified) {
        this.deviceVerified = deviceVerified;
    }

    private void setMfaType(final UserMfaType mfaType) {
        this.mfaType = mfaType;
    }

    private void setCreatedAt(final Instant createdAt) {
        this.createdAt = this.assertArgumentNotNull(createdAt, "createdAt", SHOULD_NOT_BE_NULL);
    }

    private void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = this.assertArgumentNotNull(updatedAt, "updatedAt", SHOULD_NOT_BE_NULL);
    }

    private void setValidUntil(final Instant validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return "UserMfa(" +
                "id=" + getId().value() +
                ", version=" + getVersion() +
                ", mfaEnabled=" + mfaEnabled +
                ", mfaVerified=" + mfaVerified +
                ", deviceName='" + deviceName + '\'' +
                ", deviceVerified=" + deviceVerified +
                ", mfaType=" + getMfaType().map(UserMfaType::name).orElse(null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", validUntil=" + validUntil +
                ')';
    }
}
