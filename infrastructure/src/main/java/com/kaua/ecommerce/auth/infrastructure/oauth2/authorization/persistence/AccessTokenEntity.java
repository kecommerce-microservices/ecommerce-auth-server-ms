package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "access_tokens")
public class AccessTokenEntity {

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "authorization_id", nullable = false)
    private AuthorizationEntity authorization;

    @Column(name = "token_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private String type;

    @Column(length = 1000)
    private String scopes;

    public AccessTokenEntity() {
    }

    public AccessTokenEntity(
            final String id,
            final AuthorizationEntity authorization,
            final String value,
            final Instant issuedAt,
            final Instant expiresAt,
            final String metadata,
            final String type,
            final String scopes
    ) {
        this.id = id;
        this.authorization = authorization;
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.metadata = metadata;
        this.type = type;
        this.scopes = scopes;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }
}
