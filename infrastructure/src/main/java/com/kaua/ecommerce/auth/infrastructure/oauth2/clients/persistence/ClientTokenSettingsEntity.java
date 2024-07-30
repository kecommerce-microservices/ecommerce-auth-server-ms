package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "token_settings")
public class ClientTokenSettingsEntity {

    @Id
    private String id;

    @Column(name = "access_token_ttl")
    private long accessTokenTTL;

    @Column(name = "refresh_token_ttl")
    private long refreshTokenTTL;

    @Column(name = "reuse_refresh_tokens")
    private boolean reuseRefreshTokens;

    @JsonIgnore
    @OneToOne
    private ClientEntity client;

    public ClientTokenSettingsEntity() {
    }

    public ClientTokenSettingsEntity(
            final String id,
            final long accessTokenTTL,
            final long refreshTokenTTL,
            final boolean reuseRefreshTokens,
            final ClientEntity client
    ) {
        this.id = id;
        this.accessTokenTTL = accessTokenTTL;
        this.refreshTokenTTL = refreshTokenTTL;
        this.reuseRefreshTokens = reuseRefreshTokens;
        this.client = client;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAccessTokenTTL() {
        return accessTokenTTL;
    }

    public void setAccessTokenTTL(long accessTokenTTL) {
        this.accessTokenTTL = accessTokenTTL;
    }

    public long getRefreshTokenTTL() {
        return refreshTokenTTL;
    }

    public void setRefreshTokenTTL(long refreshTokenTTL) {
        this.refreshTokenTTL = refreshTokenTTL;
    }

    public boolean isReuseRefreshTokens() {
        return reuseRefreshTokens;
    }

    public void setReuseRefreshTokens(boolean reuseRefreshTokens) {
        this.reuseRefreshTokens = reuseRefreshTokens;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
