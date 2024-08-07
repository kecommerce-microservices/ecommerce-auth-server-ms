package com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence;

import com.kaua.ecommerce.auth.domain.mailtokens.MailId;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.users.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mail_tokens")
public class MailTokenJpaEntity {

    @Id
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MailType type;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    private Long version;

    public MailTokenJpaEntity() {
    }

    private MailTokenJpaEntity(
            final UUID id,
            final String email,
            final UUID userId,
            final String token,
            final boolean isUsed,
            final MailType type,
            final Instant usedAt,
            final Instant expiresAt,
            final Instant createdAt,
            final Long version
    ) {
        this.id = id;
        this.email = email;
        this.userId = userId;
        this.token = token;
        this.isUsed = isUsed;
        this.type = type;
        this.usedAt = usedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.version = version;
    }

    public static MailTokenJpaEntity toEntity(final MailToken aMailToken) {
        return new MailTokenJpaEntity(
                aMailToken.getId().value(),
                aMailToken.getEmail(),
                aMailToken.getUserId().value(),
                aMailToken.getToken(),
                aMailToken.isUsed(),
                aMailToken.getType(),
                aMailToken.getUsedAt().orElse(null),
                aMailToken.getExpiresAt(),
                aMailToken.getCreatedAt(),
                aMailToken.getVersion()
        );
    }

    public MailToken toDomain() {
        return MailToken.with(
                new MailId(getId()),
                getVersion(),
                getEmail(),
                new UserId(getUserId()),
                getToken(),
                isUsed(),
                getType(),
                getUsedAt(),
                getExpiresAt(),
                getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public MailType getType() {
        return type;
    }

    public void setType(MailType type) {
        this.type = type;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }
}
