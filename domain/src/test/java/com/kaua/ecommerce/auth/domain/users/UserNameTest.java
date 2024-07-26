package com.kaua.ecommerce.auth.domain.users;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserNameTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewUserName_thenCreateNewUserName() {
        final var aFirstName = "John";
        final var aLastName = "Doe";

        final var userName = new UserName(aFirstName, aLastName);

        Assertions.assertEquals(aFirstName, userName.firstName());
        Assertions.assertEquals(aLastName, userName.lastName());
        Assertions.assertEquals(aFirstName + " " + aLastName, userName.fullName());
    }

    @Test
    void givenAnInvalidNullFirstName_whenCallNewUserName_thenThrowDomainException() {
        final String aFirstName = null;
        final var aLastName = "Doe";

        final var aProperty = "firstName";
        final var aErrorMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullLastName_whenCallNewUserName_thenThrowDomainException() {
        final var aFirstName = "John";
        final String aLastName = null;

        final var aProperty = "lastName";
        final var aErrorMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMinLengthFirstName_whenCallNewUserName_thenThrowDomainException() {
        final var aFirstName = "Jo";
        final var aLastName = "Doe";

        final var aProperty = "firstName";
        final var aErrorMessage = "should have at least 3 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMinLengthLastName_whenCallNewUserName_thenThrowDomainException() {
        final var aFirstName = "John";
        final var aLastName = "Do";

        final var aProperty = "lastName";
        final var aErrorMessage = "should have at least 3 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMaxLengthFirstName_whenCallNewUserName_thenThrowDomainException() {
        final var aFirstName = RandomStringUtils.generateValue(101);
        final var aLastName = "Doe";

        final var aProperty = "firstName";
        final var aErrorMessage = "should have at most 100 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidMaxLengthLastName_whenCallNewUserName_thenThrowDomainException() {
        final var aFirstName = "John";
        final var aLastName = RandomStringUtils.generateValue(101);

        final var aProperty = "lastName";
        final var aErrorMessage = "should have at most 100 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new UserName(aFirstName, aLastName));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }
}
