package com.kaua.ecommerce.auth.domain.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class InternalServerErrorException extends NoStacktraceException {

    public InternalServerErrorException(final String message) {
        super(message);
    }

    public InternalServerErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
