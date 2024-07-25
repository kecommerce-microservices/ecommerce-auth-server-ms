package com.kaua.ecommerce.auth.domain;

import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import net.datafaker.Faker;

import java.util.UUID;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {
    }

    public final class Roles {

        private Roles() {}

        public static Role defaultRole() {
            return Role.create(
                    new RoleName("customer"),
                    new RoleDescription("Customer role"),
                    true
            );
        }

        public static Role randomRole() {
            return Role.create(
                    new RoleName("role-" + UUID.randomUUID().toString()),
                    new RoleDescription(RandomStringUtils.generateValue(10)),
                    faker.bool().bool()
            );
        }
    }
}
