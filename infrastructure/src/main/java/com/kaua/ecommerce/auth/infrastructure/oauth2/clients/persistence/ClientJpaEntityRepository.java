package com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientJpaEntityRepository extends JpaRepository<ClientEntity, String> {

    @Query("SELECT c FROM ClientEntity c " +
            "LEFT JOIN FETCH c.authenticationMethods " +
            "LEFT JOIN FETCH c.grantTypes " +
            "LEFT JOIN FETCH c.redirectUrls " +
            "LEFT JOIN FETCH c.scopes " +
            "LEFT JOIN FETCH c.clientTokenSettings " +
            "LEFT JOIN FETCH c.clientSettings " +
            "WHERE c.clientId = :clientId")
    Optional<ClientEntity> findByClientId(@Param("clientId") String clientId);
}
