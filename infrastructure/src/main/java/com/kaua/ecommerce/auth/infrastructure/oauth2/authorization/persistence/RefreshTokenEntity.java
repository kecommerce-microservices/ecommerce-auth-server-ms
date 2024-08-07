package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "authorization_id", nullable = false)
    private AuthorizationEntity authorization;

    @Column(name = "refresh_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(
            final String id,
            final AuthorizationEntity authorization,
            final String value,
            final Instant issuedAt,
            final Instant expiresAt,
            final String metadata
    ) {
        this.id = id;
        this.authorization = authorization;
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthorizationEntity getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationEntity authorization) {
        this.authorization = authorization;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
