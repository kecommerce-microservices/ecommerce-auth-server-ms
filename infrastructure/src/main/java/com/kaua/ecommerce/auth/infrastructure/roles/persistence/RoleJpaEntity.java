package com.kaua.ecommerce.auth.infrastructure.roles.persistence;

import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class RoleJpaEntity {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    private Long version;

    public RoleJpaEntity() {
    }

    private RoleJpaEntity(
            final UUID id,
            final String name,
            final String description,
            final boolean isDefault,
            final boolean isDeleted,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt,
            final Long version
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isDefault = isDefault;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public static RoleJpaEntity toEntity(final Role aRole) {
        return new RoleJpaEntity(
                aRole.getId().value(),
                aRole.getName().value(),
                aRole.getDescription().value(),
                aRole.isDefault(),
                aRole.isDeleted(),
                aRole.getCreatedAt(),
                aRole.getUpdatedAt(),
                aRole.getDeletedAt().orElse(null),
                aRole.getVersion()
        );
    }

    public Role toDomain() {
        return Role.with(
                new RoleId(getId()),
                getVersion(),
                new RoleName(getName()),
                new RoleDescription(getDescription()),
                isDefault(),
                isDeleted(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt().orElse(null)
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Optional<Instant> getDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getVersion() {
        return version;
    }
}
