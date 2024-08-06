package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.application.usecases.users.*;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.*;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.*;
import com.kaua.ecommerce.auth.domain.users.UserId;
import com.kaua.ecommerce.auth.infrastructure.rest.UserRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.*;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetUserByIdResponse;
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
import java.util.stream.Collectors;

@RestController
public class UserRestController implements UserRestApi {

    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    private final CreateUserUseCase createUserUseCase;
    private final CreateUserMfaUseCase createUserMfaUseCase;
    private final ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase;
    private final DisableUserMfaUseCase disableUserMfaUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final MarkAsDeleteUserUseCase markAsDeleteUserUseCase;
    private final AddRolesToUserUseCase addRolesToUserUseCase;
    private final RemoveUserRoleUseCase removeUserRoleUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    public UserRestController(
            final CreateUserUseCase createUserUseCase,
            final CreateUserMfaUseCase createUserMfaUseCase,
            final ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase,
            final DisableUserMfaUseCase disableUserMfaUseCase,
            final UpdateUserUseCase updateUserUseCase,
            final MarkAsDeleteUserUseCase markAsDeleteUserUseCase,
            final AddRolesToUserUseCase addRolesToUserUseCase,
            final RemoveUserRoleUseCase removeUserRoleUseCase,
            final GetUserByIdUseCase getUserByIdUseCase
    ) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase);
        this.createUserMfaUseCase = Objects.requireNonNull(createUserMfaUseCase);
        this.confirmUserMfaDeviceUseCase = Objects.requireNonNull(confirmUserMfaDeviceUseCase);
        this.disableUserMfaUseCase = Objects.requireNonNull(disableUserMfaUseCase);
        this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase);
        this.markAsDeleteUserUseCase = Objects.requireNonNull(markAsDeleteUserUseCase);
        this.addRolesToUserUseCase = Objects.requireNonNull(addRolesToUserUseCase);
        this.removeUserRoleUseCase = Objects.requireNonNull(removeUserRoleUseCase);
        this.getUserByIdUseCase = Objects.requireNonNull(getUserByIdUseCase);
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
    public ResponseEntity<GetUserByIdResponse> getUserById(final String id) {
        log.debug("Received request to get user by id: {}", id);

        final var aInput = new GetUserByIdInput(UUID.fromString(id));

        final var aOutput = this.getUserByIdUseCase.execute(aInput);

        log.debug("User found: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GetUserByIdResponse(aOutput));
    }

    @Override
    public ResponseEntity<GetUserByIdResponse> getAuthenticatedUser(final UserDetailsImpl principal) {
        log.debug("Received request to get authenticated user: {}", principal.getUsername());

        final var aInput = new GetUserByIdInput(UUID.fromString(principal.getUsername()));

        final var aOutput = this.getUserByIdUseCase.execute(aInput);

        log.debug("User authenticated found: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GetUserByIdResponse(aOutput));
    }

    @Override
    public ResponseEntity<UpdateUserOutput> updateUser(
            final UserDetailsImpl principal,
            final UpdateUserRequest request
    ) {
        log.debug("Received request to update user: {}", request);

        final var aInput = new UpdateUserInput(
                UUID.fromString(principal.getUsername()),
                request.firstName(),
                request.lastName(),
                request.email()
        );

        final var aOutput = this.updateUserUseCase.execute(aInput);

        log.info("User updated successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK)
                .body(aOutput);
    }

    @Override
    public ResponseEntity<AddRolesToUserOutput> addRolesToUser(
            final String userId,
            final AddRolesToUserRequest request
    ) {
        log.debug("Received request to add roles to user: {}", request);

        final var aInput = new AddRolesToUserInput(
                UUID.fromString(userId),
                request.rolesIds().stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet())
        );

        final var aOutput = this.addRolesToUserUseCase.execute(aInput);

        log.info("Roles added to user successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK)
                .body(aOutput);
    }

    @Override
    public ResponseEntity<RemoveUserRoleOutput> removeRoleFromUser(
            final String userId,
            final String roleId
    ) {
        log.debug("Received request to remove role from user userId: {} roleId: {}", userId, roleId);

        final var aInput = new RemoveUserRoleInput(
                UUID.fromString(userId),
                UUID.fromString(roleId)
        );

        final var aOutput = this.removeUserRoleUseCase.execute(aInput);

        log.info("Role removed from user successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.OK)
                .body(aOutput);
    }

    @Override
    public void softDeleteUser(final UserDetailsImpl principal) {
        log.debug("Received request to soft delete user userId: {}", principal.getUsername());

        final var aInput = new UserId(principal.getUsername());

        this.markAsDeleteUserUseCase.execute(aInput);

        log.info("User soft deleted successfully");
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
