package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import jakarta.persistence.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Entity
@Table(name = "grant_types")
public class GrantTypeEntity {

    @Id
    private String id;

    @Column(name = "grant_type")
    private String grantType;

    @JsonIgnore
    @ManyToOne
    private ClientEntity client;

    public GrantTypeEntity() {
    }

    public GrantTypeEntity(final String id, final String grantType, final ClientEntity client) {
        this.id = id;
        this.grantType = grantType;
        this.client = client;
    }

    public static GrantTypeEntity from(
            final AuthorizationGrantType authorizationGrantType,
            final ClientEntity client
    ) {
        return new GrantTypeEntity(
                IdentifierUtils.generateNewId(),
                authorizationGrantType.getValue(),
                client
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
