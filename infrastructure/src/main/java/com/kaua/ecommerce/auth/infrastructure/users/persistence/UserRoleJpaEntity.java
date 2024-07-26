package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.domain.roles.RoleId;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users_roles")
public class UserRoleJpaEntity {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("userId")
    private UserJpaEntity user;

    public UserRoleJpaEntity() {}

    public UserRoleJpaEntity(final UserJpaEntity aUser, final RoleId aRoleId) {
        this.id = new UserRoleId(aUser.getId(), aRoleId.value());
        this.user = aUser;
    }

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserRoleJpaEntity that = (UserRoleJpaEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser());
    }
}
