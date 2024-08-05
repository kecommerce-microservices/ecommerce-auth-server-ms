package com.kaua.ecommerce.auth.domain.exceptions;

import com.kaua.ecommerce.auth.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserIsDeletedExceptionTest extends UnitTest {

    @Test
    void  givenAValidUserId_whenCallNewUserIsDeletedException_thenAnUserIsDeletedExceptionShouldBeThrown() {
        final var aId = IdentifierUtils.generateNewId();

        Assertions.assertThrows(UserIsDeletedException.class, () -> {
            throw new UserIsDeletedException(aId);
        });
    }
}
