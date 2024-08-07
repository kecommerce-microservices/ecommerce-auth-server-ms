package com.kaua.ecommerce.auth.infrastructure.mailtokens;

import com.kaua.ecommerce.auth.application.repositories.MailRepository;
import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MailRepositoryImpl implements MailRepository {

    private static final Logger log = LoggerFactory.getLogger(MailRepositoryImpl.class);

    private final MailTokenJpaEntityRepository mailTokenJpaEntityRepository;

    public MailRepositoryImpl(final MailTokenJpaEntityRepository mailTokenJpaEntityRepository) {
        this.mailTokenJpaEntityRepository = Objects.requireNonNull(mailTokenJpaEntityRepository);
    }

    @Override
    public MailToken save(final MailToken mailToken) {
        log.debug("Saving mail token: {}", mailToken);

        final var mailTokenJpaEntity = MailTokenJpaEntity.toEntity(mailToken);
        final var savedMailTokenJpaEntity = this.mailTokenJpaEntityRepository
                .save(mailTokenJpaEntity).toDomain();

        log.info("Mail token saved: {}", savedMailTokenJpaEntity);
        return savedMailTokenJpaEntity;
    }

    @Override
    public List<MailToken> findByEmail(final String email) {
        return this.mailTokenJpaEntityRepository.findByEmail(email)
                .stream()
                .map(MailTokenJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteByToken(final String token) {
        log.debug("Deleting mail token by token: {}", token);
        this.mailTokenJpaEntityRepository.deleteByToken(token);
        log.info("Mail token deleted by token: {}", token);
    }
}
