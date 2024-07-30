package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "oidc_id_tokens")
public class OidcIdTokenEntity {

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "authorization_id", nullable = false)
    private AuthorizationEntity authorization;

    @Column(name = "oidc_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(columnDefinition = "TEXT")
    private String claims;

    public OidcIdTokenEntity() {
    }

    public OidcIdTokenEntity(
            final String id,
            final AuthorizationEntity authorization,
            final String value,
            final Instant issuedAt,
            final Instant expiresAt,
            final String metadata,
            final String claims
    ) {
        this.id = id;
        this.authorization = authorization;
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.metadata = metadata;
        this.claims = claims;
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

    public String getClaims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }
}
