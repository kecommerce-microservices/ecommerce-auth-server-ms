package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.users.mfas.UserMfa;
import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class User extends AggregateRoot<UserId> {

    private static final String SHOULD_NOT_BE_NULL = "should not be null";

    private CustomerId customerId;
    private UserName name;
    private UserEmail email;
    private UserPassword password;
    private Set<RoleId> roles;
    private boolean isDeleted;
    private boolean emailVerified;
    private UserMfa mfa;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private User(
            final UserId aUserId,
            final long aVersion,
            final CustomerId aCustomerId,
            final UserName aName,
            final UserEmail aEmail,
            final UserPassword aPassword,
            final Set<RoleId> aRoles,
            final boolean aIsDeleted,
            final boolean aEmailVerified,
            final UserMfa aMfa,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aDeletedAt
    ) {
        super(aUserId, aVersion);
        this.setCustomerId(aCustomerId);
        this.setName(aName);
        this.setEmail(aEmail);
        this.setPassword(aPassword);
        this.setRoles(aRoles);
        this.setDeleted(aIsDeleted);
        this.setEmailVerified(aEmailVerified);
        this.setMfa(aMfa);
        this.setCreatedAt(aCreatedAt);
        this.setUpdatedAt(aUpdatedAt);
        this.setDeletedAt(aDeletedAt);
    }

    public static User newUser(
            final CustomerId aCustomerId,
            final UserName aName,
            final UserEmail aEmail,
            final UserPassword aPassword,
            final Set<RoleId> aRoles
    ) {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var now = InstantUtils.now();

        final var aUserMfa = UserMfa.newMfa();

        return new User(
                aUserId,
                0,
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles,
                false,
                false,
                aUserMfa,
                now,
                now,
                null
        );
    }

    public User changeName(final UserName aName) {
        this.setName(aName);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public User changeEmail(final UserEmail aEmail) {
        this.setEmail(aEmail);
        this.setUpdatedAt(InstantUtils.now());
        return this;
    }

    public static User with(
            final UserId aUserId,
            final long aVersion,
            final CustomerId aCustomerId,
            final UserName aName,
            final UserEmail aEmail,
            final UserPassword aPassword,
            final Set<RoleId> aRoles,
            final boolean aIsDeleted,
            final boolean aEmailVerified,
            final UserMfa aMfa,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aDeletedAt
    ) {
        return new User(
                aUserId,
                aVersion,
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles,
                aIsDeleted,
                aEmailVerified,
                aMfa,
                aCreatedAt,
                aUpdatedAt,
                aDeletedAt
        );
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public UserName getName() {
        return name;
    }

    public UserEmail getEmail() {
        return email;
    }

    public UserPassword getPassword() {
        return password;
    }

    public Set<RoleId> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public UserMfa getMfa() {
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

    private void setCustomerId(final CustomerId customerId) {
        this.customerId = this.assertArgumentNotNull(customerId, "customerId", SHOULD_NOT_BE_NULL);
    }

    private void setName(final UserName name) {
        this.name = this.assertArgumentNotNull(name, "name", SHOULD_NOT_BE_NULL);
    }

    private void setEmail(final UserEmail email) {
        this.email = this.assertArgumentNotNull(email, "email", SHOULD_NOT_BE_NULL);
    }

    private void setPassword(final UserPassword password) {
        this.password = this.assertArgumentNotNull(password, "password", SHOULD_NOT_BE_NULL);
    }

    private void setRoles(final Set<RoleId> roles) {
        this.assertArgumentNotEmpty(roles, "roles", "should not be empty");
        this.roles = new HashSet<>(roles);
    }

    private void setDeleted(final boolean deleted) {
        isDeleted = deleted;
    }

    private void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setMfa(final UserMfa mfa) {
        this.mfa = this.assertArgumentNotNull(mfa, "mfa", SHOULD_NOT_BE_NULL);
    }

    private void setCreatedAt(final Instant createdAt) {
        this.createdAt = this.assertArgumentNotNull(createdAt, "createdAt", SHOULD_NOT_BE_NULL);
    }

    private void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = this.assertArgumentNotNull(updatedAt, "updatedAt", SHOULD_NOT_BE_NULL);
    }

    private void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "User(" +
                "id=" + getId().value() +
                ", customerId=" + customerId.value() +
                ", name=" + name.fullName() +
                ", email=" + email.value() +
                ", roles=" + getRoles().size() +
                ", isDeleted=" + isDeleted +
                ", emailVerified=" + emailVerified +
                ", mfa=" + getMfa().toString() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + getDeletedAt().orElse(null) +
                ", version=" + getVersion() +
                ')';
    }
}
