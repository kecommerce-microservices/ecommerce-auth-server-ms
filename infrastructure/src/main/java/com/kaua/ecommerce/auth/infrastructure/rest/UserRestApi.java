package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.users.outputs.*;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.*;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetUserByIdResponse;
import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User API")
@RequestMapping(value = "/v1/users")
public interface UserRestApi {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<CreateUserOutput> createUser(@RequestBody CreateUserRequest request);

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get user by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<GetUserByIdResponse> getUserById(@PathVariable("id") final String id);

    @GetMapping(
            value = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<GetUserByIdResponse> getAuthenticatedUser(@AuthenticationPrincipal final UserDetailsImpl principal);

    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<UpdateUserOutput> updateUser(
            @AuthenticationPrincipal final UserDetailsImpl principal,
            @RequestBody UpdateUserRequest request
    );

    @PatchMapping(
            value = "/{id}/add-roles",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Add roles to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<AddRolesToUserOutput> addRolesToUser(
            @PathVariable("id") final String userId,
            @RequestBody AddRolesToUserRequest request
    );

    @PatchMapping(
            value = "/{id}/remove-role/{roleId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Remove role from user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<RemoveUserRoleOutput> removeRoleFromUser(
            @PathVariable("id") final String userId,
            @PathVariable("roleId") final String roleId
    );

    @PatchMapping(
            value = "/email-confirm/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Confirm user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User or token not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<ConfirmUserEmailOutput> confirmEmail(@PathVariable("token") final String token);

    @PatchMapping(
            value = "/password-change/{token}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User or token not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<ChangeUserPasswordOutput> changePassword(
            @PathVariable("token") final String token,
            @RequestBody ChangeUserPasswordRequest request
    );

    @DeleteMapping()
    @Operation(summary = "Soft delete authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @ResponseStatus(HttpStatus.OK)
    void softDeleteUser(@AuthenticationPrincipal final UserDetailsImpl principal);

    @PostMapping(
            value = "/mfa",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new user MFA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User MFA created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<CreateUserMfaOutput> createUserMfa(
            @AuthenticationPrincipal final UserDetailsImpl principal,
            @RequestBody CreateUserMfaRequest request
    );

    @PostMapping(
            value = "/mfa/device/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Confirm user MFA device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User MFA device confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<ConfirmUserMfaDeviceOutput> confirmDevice(
            @AuthenticationPrincipal final UserDetailsImpl principal,
            @RequestParam("otp_code") final String otpCode,
            @RequestBody ConfirmUserMfaDeviceRequest request
    );

    @PostMapping(
            value = "/mfa/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Validate OTP code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP code validated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    void verifyMfa(
            @AuthenticationPrincipal final UserDetailsImpl principal,
            @RequestParam("otp_code") final String otp
    );

    @DeleteMapping(value = "/mfa/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Disable MFA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User authenticated not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<DisableUserMfaOutput> disableMfa(@AuthenticationPrincipal final UserDetailsImpl principal);
}
