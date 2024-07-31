package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import jakarta.persistence.*;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Entity
@Table(name = "authentication_methods")
public class AuthenticationMethodEntity {

    @Id
    private String id;

    @Column(name = "authentication_method")
    private String authenticationMethod;

    @JsonIgnore
    @ManyToOne
    private ClientEntity client;

    public AuthenticationMethodEntity() {
    }

    public AuthenticationMethodEntity(
            final String id,
            final String authenticationMethod,
            final ClientEntity client
    ) {
        this.id = id;
        this.authenticationMethod = authenticationMethod;
        this.client = client;
    }

    public static AuthenticationMethodEntity from(
            final ClientAuthenticationMethod authenticationMethod,
            final ClientEntity client
    ) {
        return new AuthenticationMethodEntity(
                IdentifierUtils.generateNewId(),
                authenticationMethod.getValue(),
                client
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
