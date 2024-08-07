package com.kaua.ecommerce.auth.domain;

import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.auth.domain.users.*;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import net.datafaker.Faker;

import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {
    }

    public static final class Mails {
        private Mails() {}

        public static MailToken mail(final String aEmail, final String aUserId, final MailType aType) {
            return MailToken.newMailToken(
                    aEmail,
                    new UserId(aUserId),
                    IdentifierUtils.generateNewIdWithoutHyphen(),
                    aType,
                    InstantUtils.now().plus(3, ChronoUnit.DAYS)
            );
        }
    }

    public static final class Users {

        private Users() {
        }

        public static String email() {
            return faker.internet().emailAddress();
        }

        public static User randomUser(final RoleId aRoleId) {
            final var aFirstName = faker.name().firstName();
            final var aFirstNameValid = aFirstName.length() < 3
                    ? aFirstName + RandomStringUtils.generateValue(3 - aFirstName.length())
                    : aFirstName;

            final var aLastName = faker.name().lastName();
            final var aLastNameValid = aLastName.length() < 3
                    ? aLastName + RandomStringUtils.generateValue(3 - aLastName.length())
                    : aLastName;

            return User.newUser(
                    new CustomerId(IdentifierUtils.generateNewUUID()),
                    new UserName(aFirstNameValid, aLastNameValid),
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
                    new RoleName("customer-" + RandomStringUtils.generateValue(2)),
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
