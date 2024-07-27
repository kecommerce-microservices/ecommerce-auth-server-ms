package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserEmailTest extends UnitTest {

    @ParameterizedTest
    @CsvSource({
            "example@example.com",
            "user.name+tag+sorting@example.com",
            "user_name@example.co",
            "firstname.lastname@example.com",
            "user123@example.org",
            "user+mailbox/department=shipping@example.com"
    })
    void givenAValidEmail_whenCallNewUserEmail_thenAnInstanceUserEmailIsCreated(final String email) {
        final var userEmail = new UserEmail(email);

        Assertions.assertEquals(email, userEmail.value());
    }

    @Test
    void givenAnInvalidNullEmail_whenCallNewUserEmail_thenThrowDomainException() {
        final String email = null;

        final var aProperty = "email";
        final var aMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserEmail(email));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, aException.getErrors().get(0).message());
    }

    @ParameterizedTest
    @CsvSource({
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@com.",
            "username@domain..com",
            "username@domain.c",
            "user@domain.corporate",
            "user@domain_with_space .com",
            "user@domain@anotherdomain.com",
            "user@domain..com"
    })
    void givenAnInvalidEmail_whenCallNewUserEmail_thenThrowDomainException(final String email) {
        final var aProperty = "email";
        final var aMessage = "should be a valid email address";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserEmail(email));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, aException.getErrors().get(0).message());
    }
}
