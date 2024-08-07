package com.kaua.ecommerce.auth.domain.mailtokens;

import com.kaua.ecommerce.auth.domain.users.UserId;
import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public class MailToken extends AggregateRoot<MailId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private String email;
    private UserId userId;
    private String token;
    private boolean isUsed;
    private MailType type;
    private Instant usedAt;
    private Instant expiresAt;
    private Instant createdAt;

    private MailToken(
            final MailId aMailId,
            final long aVersion,
            final String aEmail,
            final UserId aUserId,
            final String aToken,
            final boolean aIsUsed,
            final MailType aType,
            final Instant aUsedAt,
            final Instant aExpiresAt,
            final Instant aCreatedAt
    ) {
        super(aMailId, aVersion);
        this.setEmail(aEmail);
        this.setUserId(aUserId);
        this.setToken(aToken);
        this.setUsed(aIsUsed);
        this.setType(aType);
        this.setUsedAt(aUsedAt);
        this.setExpiresAt(aExpiresAt);
        this.setCreatedAt(aCreatedAt);
    }

    public static MailToken newMailToken(
            final String email,
            final UserId userId,
            final String token,
            final MailType type,
            final Instant expiresAt
    ) {
        final var aMailId = new MailId(IdentifierUtils.generateNewUUID());
        final var aNow = InstantUtils.now();

        return new MailToken(
                aMailId,
                0,
                email,
                userId,
                token,
                false,
                type,
                null,
                expiresAt,
                aNow
        );
    }

    public boolean isExpired() {
        return InstantUtils.now().isAfter(this.expiresAt);
    }

    public MailToken markAsUsed() {
        this.setUsed(true);
        this.setUsedAt(InstantUtils.now());
        return this;
    }

    public static MailToken with(
            final MailId aMailId,
            final long aVersion,
            final String aEmail,
            final UserId aUserId,
            final String aToken,
            final boolean aIsUsed,
            final MailType aType,
            final Instant aUsedAt,
            final Instant aExpiresAt,
            final Instant aCreatedAt
    ) {
        return new MailToken(
                aMailId,
                aVersion,
                aEmail,
                aUserId,
                aToken,
                aIsUsed,
                aType,
                aUsedAt,
                aExpiresAt,
                aCreatedAt
        );
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(final String email) {
        this.email = this.assertArgumentNotEmpty(email, "email", "should not be empty");
    }

    public UserId getUserId() {
        return userId;
    }

    private void setUserId(final UserId userId) {
        this.userId = this.assertArgumentNotNull(userId, "userId", SHOULD_NOT_BE_NULL);
    }

    public String getToken() {
        return token;
    }

    private void setToken(final String token) {
        this.token = this.assertArgumentNotEmpty(token, "token", "should not be empty");
    }

    public boolean isUsed() {
        return isUsed;
    }

    private void setUsed(final boolean used) {
        isUsed = used;
    }

    public MailType getType() {
        return type;
    }

    private void setType(final MailType type) {
        this.type = this.assertArgumentNotNull(type, "type", SHOULD_NOT_BE_NULL);
    }

    public Optional<Instant> getUsedAt() {
        return Optional.ofNullable(usedAt);
    }

    private void setUsedAt(final Instant usedAt) {
        this.usedAt = usedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    private void setExpiresAt(final Instant expiresAt) {
        this.expiresAt = this.assertArgumentNotNull(expiresAt, "expiresAt", SHOULD_NOT_BE_NULL);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(final Instant createdAt) {
        this.createdAt = this.assertArgumentNotNull(createdAt, "createdAt", SHOULD_NOT_BE_NULL);
    }

    @Override
    public String toString() {
        return "MailToken(" +
                "id='" + getId().value() + '\'' +
                ", email=" + email +
                ", userId=" + userId.value() +
                ", token='" + token + '\'' +
                ", isUsed=" + isUsed +
                ", type=" + type.name() +
                ", usedAt=" + getUsedAt().orElse(null) +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", version=" + getVersion() +
                ')';
    }
}
