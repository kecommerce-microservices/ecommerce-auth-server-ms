package com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MailTokenJpaEntityRepository extends JpaRepository<MailTokenJpaEntity, UUID> {

    List<MailTokenJpaEntity> findByEmail(String email);

    void deleteByToken(String token);
}
