package com.kaua.ecommerce.auth.infrastructure.users.persistence.cache;

import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.*;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserMfaJpaEntity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserCacheEntity implements Serializable {

    private UUID id;
    private long version;
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<UUID> roles;
    private boolean isDeleted;
    private boolean emailVerified;
    private UserMfaJpaEntity mfa;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public UserCacheEntity() {
    }

    public UserCacheEntity(
            final UUID id,
            final long version,
            final UUID customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String password,
            final Set<UUID> roles,
            final boolean isDeleted,
            final boolean emailVerified,
            final UserMfaJpaEntity mfa,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.version = version;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.isDeleted = isDeleted;
        this.emailVerified = emailVerified;
        this.mfa = mfa;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static UserCacheEntity toEntity(final UserJpaEntity aEntity) {
        return new UserCacheEntity(
                aEntity.getId(),
                aEntity.getVersion(),
                aEntity.getCustomerId(),
                aEntity.getFirstName(),
                aEntity.getLastName(),
                aEntity.getEmail(),
                aEntity.getPassword(),
                aEntity.getRoles()
                        .stream()
                        .map(it -> it.getId().getRoleId())
                        .collect(Collectors.toSet()),
                aEntity.isDeleted(),
                aEntity.isEmailVerified(),
                aEntity.getMfa(),
                aEntity.getCreatedAt(),
                aEntity.getUpdatedAt(),
                aEntity.getDeletedAt()
        );
    }

    public User toDomain() {
        return User.with(
                new UserId(getId()),
                getVersion(),
                new CustomerId(getCustomerId()),
                new UserName(getFirstName(), getLastName()),
                new UserEmail(getEmail()),
                new UserPassword(getPassword()),
                getRoles().stream()
                        .map(RoleId::new).collect(Collectors.toSet()),
                isDeleted(),
                isEmailVerified(),
                getMfa().toDomain(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt().orElse(null)
        );
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<UUID> getRoles() {
        return roles;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public UserMfaJpaEntity getMfa() {
        return mfa;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<Instant> getDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }
}
