package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "client_settings")
public class ClientSettingsEntity {

    @Id
    private String id;

    @Column(name = "require_authorization_consent")
    private boolean requireAuthorizationConsent;

    @Column(name = "require_proof_key")
    private boolean requireProofKey;

    @JsonIgnore
    @OneToOne
    private ClientEntity client;

    public ClientSettingsEntity() {
    }

    public ClientSettingsEntity(
            final String id,
            final boolean requireAuthorizationConsent,
            final boolean requireProofKey,
            final ClientEntity client
    ) {
        this.id = id;
        this.requireAuthorizationConsent = requireAuthorizationConsent;
        this.requireProofKey = requireProofKey;
        this.client = client;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRequireAuthorizationConsent() {
        return requireAuthorizationConsent;
    }

    public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
        this.requireAuthorizationConsent = requireAuthorizationConsent;
    }

    public boolean isRequireProofKey() {
        return requireProofKey;
    }

    public void setRequireProofKey(boolean requireProofKey) {
        this.requireProofKey = requireProofKey;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
