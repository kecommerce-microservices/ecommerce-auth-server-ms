package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserOutput;
import com.kaua.ecommerce.auth.infrastructure.rest.UserRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class UserRestController implements UserRestApi {

    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    private final CreateUserUseCase createUserUseCase;

    public UserRestController(final CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
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
}
