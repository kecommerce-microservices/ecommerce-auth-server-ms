package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserPasswordTest extends UnitTest {

    @ParameterizedTest
    @CsvSource({
            "123456Am*",
            "Am@123456",
            "123456Am@",
            "12345678mA!@",
    })
    void givenAValidPassword_whenCreateUserPassword_thenPasswordIsCreated(String password) {
        final var userPassword = UserPassword.create(password);
        Assertions.assertEquals(password, userPassword.value());
    }

    @ParameterizedTest
    @CsvSource({
            "12345678",
            "12345678a",
            "12345678A",
            "12345678@",
            "12345678aA",
            "12345678a@",
            "12345678A@"
    })
    void givenAnInvalidPassword_whenCreateUserPassword_thenThrowDomainException(String password) {
        final var aProperty = "password";
        final var aMessage = "should have at least one lowercase letter, one uppercase letter, one digit and one special character";

        final var exception = Assertions.assertThrows(DomainException.class,
                () -> UserPassword.create(password));

        Assertions.assertEquals(aProperty, exception.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullPassword_whenCreateUserPassword_thenThrowDomainException() {
        final var aProperty = "password";
        final var aMessage = "should not be empty";

        final var exception = Assertions.assertThrows(DomainException.class,
                () -> UserPassword.create(null));

        Assertions.assertEquals(aProperty, exception.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMinLengthPassword_whenCreateUserPassword_thenThrowDomainException() {
        final var aProperty = "password";
        final var aMessage = "should have at least 8 characters";

        final var exception = Assertions.assertThrows(DomainException.class,
                () -> UserPassword.create("1234567"));

        Assertions.assertEquals(aProperty, exception.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMaxLengthPassword_whenCreateUserPassword_thenThrowDomainException() {
        final var aPassword = RandomStringUtils.generateValue(256);
        final var aProperty = "password";
        final var aMessage = "should have at most 255 characters";

        final var exception = Assertions.assertThrows(DomainException.class,
                () -> UserPassword.create(aPassword));

        Assertions.assertEquals(aProperty, exception.getErrors().get(0).property());
        Assertions.assertEquals(aMessage, exception.getErrors().get(0).message());
    }
}
