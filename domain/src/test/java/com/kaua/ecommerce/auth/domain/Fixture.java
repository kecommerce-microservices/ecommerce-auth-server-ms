package com.kaua.ecommerce.auth.domain;

import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.auth.domain.users.*;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import net.datafaker.Faker;

import java.util.Set;
import java.util.UUID;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {}

    public static final class Users {

        private Users() {
        }

        public static String email() {
            return faker.internet().emailAddress();
        }

        public static User randomUser(final RoleId aRoleId) {
            return User.newUser(
                    new CustomerId(IdentifierUtils.generateNewUUID()),
                    new UserName(faker.name().firstName(), faker.name().lastName()),
                    new UserEmail(email()),
                    new UserPassword("12345678Ab*"),
                    Set.of(aRoleId)
            );
        }
    }

    public static final class Roles {

        private Roles() {
        }

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
