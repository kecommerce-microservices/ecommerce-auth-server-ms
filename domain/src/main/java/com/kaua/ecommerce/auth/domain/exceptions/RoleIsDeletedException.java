package com.kaua.ecommerce.auth.domain.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

import java.util.Collections;

public class RoleIsDeletedException extends DomainException {

    public RoleIsDeletedException(String aRoleId) {
        super("Role with id %s is deleted".formatted(aRoleId), Collections.emptyList());
    }
}
