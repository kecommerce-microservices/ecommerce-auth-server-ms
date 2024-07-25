package com.kaua.ecommerce.auth.application.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class UseCaseInputCannotBeNullException extends NoStacktraceException {

    public UseCaseInputCannotBeNullException(String useCaseName) {
        super("Input to %s cannot be null".formatted(useCaseName));
    }
}
