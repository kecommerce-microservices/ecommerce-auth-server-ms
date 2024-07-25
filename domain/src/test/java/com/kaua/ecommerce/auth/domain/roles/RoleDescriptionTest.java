package com.kaua.ecommerce.auth.domain.roles;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RoleDescriptionTest extends UnitTest {

    @Test
    void givenAValidValue_whenInstantiateRoleDescription_thenAnObjectShouldBeCreated() {
        final var aDescription = RandomStringUtils.generateValue(10);

        final var aRoleDescription = new RoleDescription(aDescription);

        Assertions.assertEquals(aDescription, aRoleDescription.value());
    }

    @Test
    void givenAValueEmpty_whenInstantiateRoleDescription_thenAnObjectShouldBeCreated() {
        final var aDescription = "";

        final var aRoleDescription = new RoleDescription(aDescription);

        Assertions.assertEquals(aDescription, aRoleDescription.value());
    }

    @Test
    void givenANullValue_whenInstantiateRoleDescription_thenAnDomainExceptionShouldBeThrown() {
        final var expectedPropertyName = "description";
        final var expectedErrorMessage = "should not be null";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleDescription(null));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAValueWithMoreThan255Characters_whenInstantiateRoleDescription_thenAnDomainExceptionShouldBeThrown() {
        final var aDescription = RandomStringUtils.generateValue(256);
        final var expectedPropertyName = "description";
        final var expectedErrorMessage = "should have at most 255 characters";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> new RoleDescription(aDescription));

        Assertions.assertEquals(expectedPropertyName, aException.getErrors().get(0).property());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
