package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.application.usecases.users.ConfirmUserMfaDeviceUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.DisableUserMfaUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserMfaDeviceInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserMfaInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.DisableUserMfaInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.ConfirmUserMfaDeviceOutput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserMfaOutput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserOutput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.DisableUserMfaOutput;
import com.kaua.ecommerce.auth.infrastructure.rest.UserRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.ConfirmUserMfaDeviceRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateUserMfaRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateUserRequest;
import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@RestController
public class UserRestController implements UserRestApi {

    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    private final CreateUserUseCase createUserUseCase;
    private final CreateUserMfaUseCase createUserMfaUseCase;
    private final ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase;
    private final DisableUserMfaUseCase disableUserMfaUseCase;

    public UserRestController(
            final CreateUserUseCase createUserUseCase,
            final CreateUserMfaUseCase createUserMfaUseCase,
            final ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase,
            final DisableUserMfaUseCase disableUserMfaUseCase
    ) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
        this.createUserMfaUseCase = Objects.requireNonNull(createUserMfaUseCase);
        this.confirmUserMfaDeviceUseCase = Objects.requireNonNull(confirmUserMfaDeviceUseCase);
        this.disableUserMfaUseCase = Objects.requireNonNull(disableUserMfaUseCase);
    }

    @Override
    public ResponseEntity<CreateUserOutput> createUser(final CreateUserRequest request) {
        log.debug("Received request to create user: {}", request);

        final var aInput = new CreateUserInput(
                request.customerId(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );

        final var aOutput = this.createUserUseCase.execute(aInput);

        log.info("User created successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/v1/users/" + aOutput.userId()))
                .body(aOutput);
    }

    @Override
    public ResponseEntity<CreateUserMfaOutput> createUserMfa(
            final UserDetailsImpl principal,
            final CreateUserMfaRequest request
    ) {
        log.debug("Received request to create user MFA userId:{} request: {}", principal.getUsername(), request);

        final var aInput = new CreateUserMfaInput(
                UUID.fromString(principal.getUsername()),
                request.type(),
                request.deviceName()
        );

        final var aOutput = this.createUserMfaUseCase.execute(aInput);

        log.info("User MFA created successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aOutput);
    }

    @Override
    public ResponseEntity<ConfirmUserMfaDeviceOutput> confirmDevice(
            final UserDetailsImpl principal,
            final String otpCode,
            final ConfirmUserMfaDeviceRequest request
    ) {
        log.debug("Received request to confirm user MFA device to userId: {}", principal.getUsername());

        final var aInput = new ConfirmUserMfaDeviceInput(
                UUID.fromString(principal.getUsername()),
                otpCode,
                InstantUtils.fromString(request.validUntil()).orElse(null)
        );

        final var aOutput = this.confirmUserMfaDeviceUseCase.execute(aInput);

        log.info("User MFA device confirmed successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK).body(aOutput);
    }

    @Override
    public void verifyMfa(
            final UserDetailsImpl principal,
            final String otp
    ) {
        log.debug("Received request to verify user MFA using authentication provider to userId: {}", principal.getUsername());
    }

    @Override
    public ResponseEntity<DisableUserMfaOutput> disableMfa(final UserDetailsImpl principal) {
        log.debug("Received request to disable user MFA userId: {}", principal.getUsername());

        final var aInput = new DisableUserMfaInput(UUID.fromString(principal.getUsername()));

        final var aOutput = this.disableUserMfaUseCase.execute(aInput);

        log.info("User MFA disabled successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK).body(aOutput);
    }
}
