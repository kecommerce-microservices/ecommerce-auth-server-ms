package com.kaua.ecommerce.auth.infrastructure.users;

import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final UserJpaEntityRepository userJpaEntityRepository;

    public UserRepositoryImpl(final UserJpaEntityRepository userJpaEntityRepository) {
        this.userJpaEntityRepository = Objects.requireNonNull(userJpaEntityRepository);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user);
        final var aOutput = this.userJpaEntityRepository.save(UserJpaEntity.toEntity(user))
                .toDomain();
        log.info("User saved: {}", aOutput);
        return aOutput;
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userJpaEntityRepository.existsByEmail(email);
    }
}