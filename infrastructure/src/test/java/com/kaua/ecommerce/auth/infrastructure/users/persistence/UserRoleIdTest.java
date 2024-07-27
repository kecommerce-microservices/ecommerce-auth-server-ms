package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.infrastructure.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@UnitTest
class UserRoleIdTest {

    @Test
    void givenAValidValues_whenCallUserRoleIdSetters_thenGettersShouldReturnTheSameValues() {
        final var userId = UUID.randomUUID();
        final var roleId = UUID.randomUUID();
        final var userRoleId = new UserRoleId();

        userRoleId.setUserId(userId);
        userRoleId.setRoleId(roleId);

        Assertions.assertEquals(userId, userRoleId.getUserId());
        Assertions.assertEquals(roleId, userRoleId.getRoleId());
    }

    @Test
    void testEquals_SameObject() {
        UserRoleId id1 = new UserRoleId(UUID.randomUUID(), UUID.randomUUID());
        final var aOutput = id1.equals(id1);
        Assertions.assertTrue(aOutput);
    }

    @Test
    void testEquals_NullObject() {
        UserRoleId id1 = new UserRoleId(UUID.randomUUID(), UUID.randomUUID());
        final var aOutput = id1.equals(null);
        Assertions.assertFalse(aOutput);
    }

    @Test
    void testEquals_DifferentClass() {
        UserRoleId id1 = new UserRoleId(UUID.randomUUID(), UUID.randomUUID());
        String notAUserRoleId = "NotAUserRoleId";
        final var aOutput = id1.equals(notAUserRoleId);
        Assertions.assertFalse(aOutput);
    }

    @Test
    void testEquals_EqualObjects() {
        final var userId = UUID.randomUUID();
        final var roleId = UUID.randomUUID();
        UserRoleId id1 = new UserRoleId(userId, roleId);
        UserRoleId id2 = new UserRoleId(userId, roleId);
        final var aOutput = id1.equals(id2);
        Assertions.assertTrue(aOutput);
    }

    @Test
    void testEquals_DifferentUserId() {
        final var roleId = UUID.randomUUID();
        UserRoleId id1 = new UserRoleId(UUID.randomUUID(), roleId);
        UserRoleId id2 = new UserRoleId(UUID.randomUUID(), roleId);
        final var aOutput = id1.equals(id2);
        Assertions.assertFalse(aOutput);
    }

    @Test
    void testEquals_DifferentRoleId() {
        final var userId = UUID.randomUUID();
        UserRoleId id1 = new UserRoleId(userId, UUID.randomUUID());
        UserRoleId id2 = new UserRoleId(userId, UUID.randomUUID());
        final var aOutput = id1.equals(id2);
        Assertions.assertFalse(aOutput);
    }

    @Test
    void testHashCode_EqualObjects() {
        final var userId = UUID.randomUUID();
        final var roleId = UUID.randomUUID();
        UserRoleId id1 = new UserRoleId(userId, roleId);
        UserRoleId id2 = new UserRoleId(userId, roleId);
        Assertions.assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testHashCode_DifferentObjects() {
        UserRoleId id1 = new UserRoleId(UUID.randomUUID(), UUID.randomUUID());
        UserRoleId id2 = new UserRoleId(UUID.randomUUID(), UUID.randomUUID());
        Assertions.assertNotEquals(id1.hashCode(), id2.hashCode());
    }
}
