package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaEntityRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);
}
