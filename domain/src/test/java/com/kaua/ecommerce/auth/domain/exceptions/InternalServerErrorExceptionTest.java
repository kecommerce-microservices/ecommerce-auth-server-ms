package com.kaua.ecommerce.auth.domain.exceptions;

import com.kaua.ecommerce.auth.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InternalServerErrorExceptionTest extends UnitTest {

    @Test
    void givenAValidMessage_whenCreateAnInternalServerException_thenAnInternalServerExceptionShouldBeCreated() {
        final var message = "Internal Server Error";

        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            throw new InternalServerErrorException(message);
        });
    }

    @Test
    void givenAValidMessageAndCause_whenCreateAnInternalServerException_thenAnInternalServerExceptionShouldBeCreated() {
        final var message = "Internal Server Error";
        final var cause = new RuntimeException("Internal Server Error");

        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            throw new InternalServerErrorException(message, cause);
        });
    }
}
