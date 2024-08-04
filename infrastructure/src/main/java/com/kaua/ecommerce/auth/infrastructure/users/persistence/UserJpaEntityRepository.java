package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaEntityRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.mfa WHERE u.email = :email")
    Optional<UserJpaEntity> findByEmail(String email);
}
