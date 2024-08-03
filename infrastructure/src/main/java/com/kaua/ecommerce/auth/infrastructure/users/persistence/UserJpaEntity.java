package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UserRoleJpaEntity> roles;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "mfa_id")
    private UserMfaJpaEntity mfa;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    private Long version;

    public UserJpaEntity() {
    }

    public UserJpaEntity(
            final UUID id,
            final UUID customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String password,
            final boolean isDeleted,
            final boolean emailVerified,
            final UserMfaJpaEntity mfaJpaEntity,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt,
            final Long version
    ) {
        this.id = id;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
        this.isDeleted = isDeleted;
        this.emailVerified = emailVerified;
        this.mfa = mfaJpaEntity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public static UserJpaEntity toEntity(final User aUser) {
        final var aEntity = new UserJpaEntity(
                aUser.getId().value(),
                aUser.getCustomerId().value(),
                aUser.getName().firstName(),
                aUser.getName().lastName(),
                aUser.getEmail().value(),
                aUser.getPassword().value(),
                aUser.isDeleted(),
                aUser.isEmailVerified(),
                UserMfaJpaEntity.toEntity(aUser.getMfa()),
                aUser.getCreatedAt(),
                aUser.getUpdatedAt(),
                aUser.getDeletedAt().orElse(null),
                aUser.getVersion()
        );

        aUser.getRoles().forEach(aEntity::addRole);

        return aEntity;
    }

    public User toDomain() {
        return User.with(
                new UserId(getId()),
                getVersion(),
                new CustomerId(getCustomerId()),
                new UserName(getFirstName(), getLastName()),
                new UserEmail(getEmail()),
                new UserPassword(getPassword()),
                getRoles().stream().map(it -> new RoleId(it.getId().getRoleId()))
                        .collect(Collectors.toSet()),
                isDeleted(),
                isEmailVerified(),
                getMfa().toDomain(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt()
        );
    }

    public void addRole(final RoleId aRoleId) {
        this.roles.add(new UserRoleJpaEntity(this, aRoleId));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserRoleJpaEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRoleJpaEntity> roles) {
        this.roles = roles;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public UserMfaJpaEntity getMfa() {
        return mfa;
    }

    public void setMfa(UserMfaJpaEntity mfa) {
        this.mfa = mfa;
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getVersion() {
        return version;
    }
}
