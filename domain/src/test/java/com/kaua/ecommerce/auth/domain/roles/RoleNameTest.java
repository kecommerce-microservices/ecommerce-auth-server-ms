package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RoleNameTest extends UnitTest {

    @Test
    void givenAValidValue_whenInstantiateRoleName_thenAnObjectShouldBeCreated() {
        final var aName = "ROLE_USER";
        final var aRoleName = new RoleName(aName);

        Assertions.assertEquals(aName, aRoleName.value());
    }

    @Test
    void givenANullValue_whenInstantiateRoleName_thenAnDomainExceptionShouldBeThrown() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleName(null));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnEmptyValue_whenInstantiateRoleName_thenAnDomainExceptionShouldBeThrown() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleName(""));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAValueWithLessThan3Characters_whenInstantiateRoleName_thenAnDomainExceptionShouldBeThrown() {
        final var aName = RandomStringUtils.generateValue(2);
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "should have at least 3 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleName(aName));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAValueWithMoreThan100Characters_whenInstantiateRoleName_thenAnDomainExceptionShouldBeThrown() {
        final var aName = RandomStringUtils.generateValue(101);
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "should have at most 100 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleName(aName));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
