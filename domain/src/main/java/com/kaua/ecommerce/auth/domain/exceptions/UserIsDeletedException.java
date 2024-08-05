package com.kaua.ecommerce.auth.domain.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

import java.util.Collections;

public class UserIsDeletedException extends DomainException {

    public UserIsDeletedException(String aUserId) {
        super("User with id %s is deleted".formatted(aUserId), Collections.emptyList());
    }
}
