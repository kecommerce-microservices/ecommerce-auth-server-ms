package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import jakarta.persistence.*;

@Entity
@Table(name = "redirect_urls")
public class RedirectUrlEntity {

    @Id
    private String id;

    @Column(name = "url", nullable = false)
    private String url;

    @JsonIgnore
    @ManyToOne
    private ClientEntity client;

    public RedirectUrlEntity() {
    }

    public RedirectUrlEntity(
            final String id,
            final String url,
            final ClientEntity client
    ) {
        this.id = id;
        this.url = url;
        this.client = client;
    }

    public static RedirectUrlEntity from(final String url, final ClientEntity client) {
        return new RedirectUrlEntity(
                IdentifierUtils.generateNewId(),
                url,
                client
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
