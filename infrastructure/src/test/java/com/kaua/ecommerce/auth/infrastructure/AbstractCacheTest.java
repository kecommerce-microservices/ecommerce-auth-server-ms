package com.kaua.ecommerce.auth.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractCacheTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractCacheTest.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clearCache() {
        log.info("Clearing cache...");
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379)
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1));

    static {
        REDIS.start();
    }

    @DynamicPropertySource
    public static void redisProperties(final DynamicPropertyRegistry registry) {
        final var redisHost = REDIS.getHost();
        final var redisPort = REDIS.getFirstMappedPort();
        final var redisExposedPorts = REDIS.getExposedPorts();

        log.info("Redis properties: host={}, port={}, exposedPorts={}", redisHost, redisPort, redisExposedPorts);

        registry.add("redis.hosts", () -> redisHost);
        registry.add("redis.ports", () -> redisPort);
    }
}
