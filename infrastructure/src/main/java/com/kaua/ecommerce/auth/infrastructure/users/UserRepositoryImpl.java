package com.kaua.ecommerce.auth.infrastructure.users;

import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.cache.UserCacheEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private static final String USER_CACHE_KEY = "users:";
    private static final String USER_EMAIL_CACHE_KEY = "users:email:";

    private static final TimeUnit CACHE_EXPIRATION_TIME_UNIT = TimeUnit.DAYS;
    private static final long CACHE_EXPIRATION_TIME = 2;

    private final UserJpaEntityRepository userJpaEntityRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public UserRepositoryImpl(
            final UserJpaEntityRepository userJpaEntityRepository,
            final RedisTemplate<String, String> redisTemplate
    ) {
        this.userJpaEntityRepository = Objects.requireNonNull(userJpaEntityRepository);
        this.redisTemplate = Objects.requireNonNull(redisTemplate);
    }

    @Override
    public User save(final User user) {
        log.debug("Saving user: {}", user);

        final var aOutput = this.userJpaEntityRepository.save(UserJpaEntity.toEntity(user))
                .toDomain();
        log.info("User saved: {}", aOutput);
        return aOutput;
    }

    @Override
    public User update(final User user) {
        log.debug("Updating user: {}", user);

        this.redisTemplate.delete(USER_CACHE_KEY.concat(user.getId().value().toString()));
        this.redisTemplate.delete(USER_EMAIL_CACHE_KEY.concat(user.getEmail().value()));

        final var aOutput = this.userJpaEntityRepository.save(UserJpaEntity.toEntity(user))
                .toDomain();
        log.info("User updated: {}", aOutput);
        return aOutput;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(final UUID id) {
        final var aKey = USER_CACHE_KEY.concat(id.toString());

        final var aCachedUser = this.redisTemplate.opsForValue().get(aKey);

        if (aCachedUser != null) {
            log.debug("User by id found in cache: {}", id);
            final var aUserCacheEntity = Json.readValue(aCachedUser, UserCacheEntity.class);
            return Optional.of(aUserCacheEntity.toDomain());
        }

        return this.userJpaEntityRepository.findById(id)
                .map(it -> {
                    log.debug("User by id found in database and set in cache: {}", id);

                    final var aUserCacheEntity = UserCacheEntity.toEntity(it);

                    this.redisTemplate.opsForValue()
                            .set(USER_CACHE_KEY.concat(it.getId().toString()), Json.writeValueAsString(aUserCacheEntity));
                    this.redisTemplate.opsForValue()
                            .set(USER_EMAIL_CACHE_KEY.concat(it.getEmail()), it.getId().toString());

                    this.redisTemplate.expire(USER_CACHE_KEY.concat(it.getId().toString()),
                            CACHE_EXPIRATION_TIME, CACHE_EXPIRATION_TIME_UNIT);
                    this.redisTemplate.expire(USER_EMAIL_CACHE_KEY.concat(it.getEmail()),
                            CACHE_EXPIRATION_TIME, CACHE_EXPIRATION_TIME_UNIT);

                    return it.toDomain();
                });
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(final String email) {
        final var aEmailKey = USER_EMAIL_CACHE_KEY.concat(email);

        final var aCachedId = this.redisTemplate.opsForValue().get(aEmailKey);

        if (aCachedId != null) {
            log.debug("User found in cache by email: {}", email);
            final var aCachedUser = this.redisTemplate.opsForValue().get(USER_CACHE_KEY.concat(aCachedId));

            if (aCachedUser != null) {
                log.debug("User by reference key found in cache: {}", aCachedId);
                final var aUserCacheEntity = Json.readValue(aCachedUser, UserCacheEntity.class);
                return Optional.of(aUserCacheEntity.toDomain());
            }
        }

        return this.userJpaEntityRepository.findByEmail(email)
                .map(it -> {
                    log.debug("User by email found in database and set in cache, {}", it.getId());
                    final var aUserCacheEntity = UserCacheEntity.toEntity(it);

                    this.redisTemplate.opsForValue()
                            .set(USER_CACHE_KEY.concat(it.getId().toString()), Json.writeValueAsString(aUserCacheEntity));
                    this.redisTemplate.opsForValue()
                            .set(USER_EMAIL_CACHE_KEY.concat(it.getEmail()), it.getId().toString());

                    this.redisTemplate.expire(USER_CACHE_KEY.concat(it.getId().toString()),
                            CACHE_EXPIRATION_TIME, CACHE_EXPIRATION_TIME_UNIT);
                    this.redisTemplate.expire(USER_EMAIL_CACHE_KEY.concat(it.getEmail()),
                            CACHE_EXPIRATION_TIME, CACHE_EXPIRATION_TIME_UNIT);

                    return it.toDomain();
                });
    }

    @Override
    public boolean existsByEmail(final String email) {
        return this.userJpaEntityRepository.existsByEmail(email);
    }
}
