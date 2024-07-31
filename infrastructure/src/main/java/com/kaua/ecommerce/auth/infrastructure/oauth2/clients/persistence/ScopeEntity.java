package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import jakarta.persistence.*;

@Entity
@Table(name = "scopes")
public class ScopeEntity {

    @Id
    private String id;

    @Column(name = "scope", nullable = false)
    private String scope;

    @JsonIgnore
    @ManyToOne
    private ClientEntity client;

    public ScopeEntity() {
    }

    public ScopeEntity(final String id, final String scope, final ClientEntity client) {
        this.id = id;
        this.scope = scope;
        this.client = client;
    }

    public static ScopeEntity from(String scope, ClientEntity client) {
        return new ScopeEntity(
                IdentifierUtils.generateNewId(),
                scope,
                client
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
