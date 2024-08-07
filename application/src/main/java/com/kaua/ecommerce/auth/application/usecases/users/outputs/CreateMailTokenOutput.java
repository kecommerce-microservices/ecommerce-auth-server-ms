package com.kaua.ecommerce.auth.application.usecases.users.outputs;

import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;

public record CreateMailTokenOutput(
        String userId,
        String mailId,
        String type
) {

    public CreateMailTokenOutput(final MailToken aMailToken) {
        this(
                aMailToken.getUserId().value().toString(),
                aMailToken.getId().value().toString(),
                aMailToken.getType().name()
        );
    }
}
