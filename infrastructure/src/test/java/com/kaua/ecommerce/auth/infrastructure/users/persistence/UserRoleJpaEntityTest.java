package com.kaua.ecommerce.auth.infrastructure.users.persistence;

import com.kaua.ecommerce.auth.infrastructure.UnitTest;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UnitTest
class UserRoleJpaEntityTest {

    @Test
    void givenAValidValues_whenCallUserRoleJpaEntitySets_thenCreateUserRoleJpaEntity() {
        final var aUserRoleEntity = new UserRoleJpaEntity();
        aUserRoleEntity.setId(new UserRoleId(
                IdentifierUtils.generateNewUUID(),
                IdentifierUtils.generateNewUUID()
        ));
        aUserRoleEntity.setUser(new UserJpaEntity());

        Assertions.assertNotNull(aUserRoleEntity);
        Assertions.assertNotNull(aUserRoleEntity.getId());
        Assertions.assertNotNull(aUserRoleEntity.getUser());
    }

    @Test
    void testEqualsUserRoleJpaEntity() {
        final var aUserRoleEntity = new UserRoleJpaEntity();
        aUserRoleEntity.setId(new UserRoleId(
                IdentifierUtils.generateNewUUID(),
                IdentifierUtils.generateNewUUID()
        ));
        aUserRoleEntity.setUser(new UserJpaEntity());

        final var aUserRoleEntity2 = new UserRoleJpaEntity();
        aUserRoleEntity2.setId(new UserRoleId(
                IdentifierUtils.generateNewUUID(),
                IdentifierUtils.generateNewUUID()
        ));
        aUserRoleEntity2.setUser(new UserJpaEntity());

        final var aOutput1 = aUserRoleEntity.equals(aUserRoleEntity2);
        final var aOutput2 = aUserRoleEntity.equals(aUserRoleEntity);
        final var aOutput3 = aUserRoleEntity.equals(null);
        final var aOutput4 = aUserRoleEntity.equals(new UserRoleJpaEntity());
        final var aOutput5 = aUserRoleEntity.equals(new Object());

        Assertions.assertFalse(aOutput1);
        Assertions.assertTrue(aOutput2);
        Assertions.assertFalse(aOutput3);
        Assertions.assertFalse(aOutput4);
        Assertions.assertFalse(aOutput5);
        Assertions.assertEquals(aUserRoleEntity.hashCode(), aUserRoleEntity.hashCode());
    }
}
