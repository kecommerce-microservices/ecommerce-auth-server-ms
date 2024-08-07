package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.application.usecases.users.CreateMailTokenUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateMailTokenInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateMailTokenOutput;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.infrastructure.rest.MailTokenRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateMailTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MailTokenRestController implements MailTokenRestApi {

    private static final Logger log = LoggerFactory.getLogger(MailTokenRestController.class);

    private final CreateMailTokenUseCase createMailTokenUseCase;

    public MailTokenRestController(final CreateMailTokenUseCase createMailTokenUseCase) {
        this.createMailTokenUseCase = Objects.requireNonNull(createMailTokenUseCase);
    }

    @Override
    public ResponseEntity<CreateMailTokenOutput> createEmailConfirmationToken(
            final CreateMailTokenRequest request
    ) {
        log.debug("Received request to create email confirmation token: {}", request);

        final var aInput = new CreateMailTokenInput(
                request.email(),
                MailType.EMAIL_CONFIRMATION.name()
        );

        final var aOutput = this.createMailTokenUseCase.execute(aInput);
        log.info("Email confirmation token created: {}", aOutput);
        return ResponseEntity.status(HttpStatus.CREATED).body(aOutput);
    }

    @Override
    public ResponseEntity<CreateMailTokenOutput> createPasswordResetToken(
            final CreateMailTokenRequest request
    ) {
        log.debug("Received request to create password reset token: {}", request);

        final var aInput = new CreateMailTokenInput(
                request.email(),
                MailType.PASSWORD_RESET.name()
        );

        final var aOutput = this.createMailTokenUseCase.execute(aInput);
        log.info("Password reset token created: {}", aOutput);
        return ResponseEntity.status(HttpStatus.CREATED).body(aOutput);
    }
}
