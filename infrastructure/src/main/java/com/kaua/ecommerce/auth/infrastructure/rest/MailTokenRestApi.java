package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateMailTokenOutput;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateMailTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Mail Token", description = "Mail Token API")
@RequestMapping(value = "/v1/mail-tokens")
public interface MailTokenRestApi {

    @PostMapping(
            value = "/email-confirmation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a email confirmation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmation token created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<CreateMailTokenOutput> createEmailConfirmationToken(@RequestBody CreateMailTokenRequest request);

    @PostMapping(
            value = "/password-reset",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a password reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset token created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<CreateMailTokenOutput> createPasswordResetToken(@RequestBody CreateMailTokenRequest request);
}
