package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.auth.domain.exceptions.RoleIsDeletedException;
import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Optional;

public class Role extends AggregateRoot<RoleId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private RoleName name;
    private RoleDescription description;
    private boolean isDefault;
    private boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Role(
            final RoleId aRoleId,
            final long aVersion,
            final RoleName aName,
            final RoleDescription aDescription,
            final boolean aIsDefault,
            final boolean aIsDeleted,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aDeletedAt
    ) {
        super(aRoleId, aVersion);
        this.setName(aName);
        this.setDescription(aDescription);
        this.setDefault(aIsDefault);
        this.setDeleted(aIsDeleted);
        this.setCreatedAt(aCreatedAt);
        this.setUpdatedAt(aUpdatedAt);
        this.setDeletedAt(aDeletedAt);
    }

    public static Role create(
            final RoleName aName,
            final RoleDescription aDescription,
            final boolean aIsDefault
    ) {
        final RoleId roleId = new RoleId(IdentifierUtils.generateNewUUID());
        final Instant now = InstantUtils.now();
        return new Role(roleId, 0, aName, aDescription, aIsDefault, false, now, now, null);
    }

    public static Role with(
            final RoleId aRoleId,
            final long aVersion,
            final RoleName aName,
            final RoleDescription aDescription,
            final boolean aIsDefault,
            final boolean aIsDeleted,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aDeletedAt
    ) {
        return new Role(aRoleId, aVersion, aName, aDescription, aIsDefault, aIsDeleted, aCreatedAt, aUpdatedAt, aDeletedAt);
    }

    public Role update(
            final RoleName aName,
            final RoleDescription aDescription,
            final boolean aIsDefault
    ) {
        if (isDeleted()) {
            throw new RoleIsDeletedException(getId().value().toString());
        }

        this.setName(aName);
        this.setDescription(aDescription);
        this.setDefault(aIsDefault);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public Role markAsDeleted() {
        if (isDeleted()) {
            throw new RoleIsDeletedException(getId().value().toString());
        }

        this.setDeleted(true);
        this.setDeletedAt(InstantUtils.now());
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public RoleName getName() {
        return name;
    }

    private void setName(final RoleName name) {
        this.name = this.assertArgumentNotNull(name, "name", SHOULD_NOT_BE_NULL);
    }

    public RoleDescription getDescription() {
        return description;
    }

    private void setDescription(final RoleDescription description) {
        this.description = description != null ? description : new RoleDescription("");
    }

    public boolean isDefault() {
        return isDefault;
    }

    private void setDefault(final boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    private void setDeleted(final boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(final Instant createdAt) {
        this.createdAt = this.assertArgumentNotNull(createdAt, "createdAt", SHOULD_NOT_BE_NULL);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = this.assertArgumentNotNull(updatedAt, "updatedAt", SHOULD_NOT_BE_NULL);
    }

    public Optional<Instant> getDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    private void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Role(" +
                "id=" + getId().value() +
                ", version=" + getVersion() +
                ", name=" + name.value() +
                ", description=" + description.value() +
                ", isDefault=" + isDefault +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                ')';
    }
}
