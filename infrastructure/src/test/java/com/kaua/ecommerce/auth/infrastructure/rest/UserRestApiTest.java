package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.users.*;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.ConfirmUserMfaDeviceInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserMfaInput;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.UpdateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.*;
import com.kaua.ecommerce.auth.infrastructure.ApiTest;
import com.kaua.ecommerce.auth.infrastructure.ControllerTest;
import com.kaua.ecommerce.auth.infrastructure.rest.controllers.UserRestController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = UserRestController.class)
class UserRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private CreateUserMfaUseCase createUserMfaUseCase;

    @MockBean
    private ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase;

    @MockBean
    private DisableUserMfaUseCase disableUserMfaUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    @MockBean
    private MarkAsDeleteUserUseCase markAsDeleteUserUseCase;

    @MockBean
    private AddRolesToUserUseCase addRolesToUserUseCase;

    @MockBean
    private RemoveUserRoleUseCase removeUserRoleUseCase;

    @Captor
    private ArgumentCaptor<CreateUserInput> createUserInputCaptor;

    @Captor
    private ArgumentCaptor<CreateUserMfaInput> createUserMfaInputCaptor;

    @Captor
    private ArgumentCaptor<ConfirmUserMfaDeviceInput> confirmUserMfaDeviceInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateUserInput> updateUserInputCaptor;

    @Test
    void givenAValidRequest_whenCallCreateUser_thenReturnUserId() throws Exception {
        final var aCustomerId = UUID.randomUUID().toString();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "teste@tesss.com";
        final var aPassword = "1233546Ab*";

        final var aExpectedUserId = UUID.randomUUID();

        Mockito.when(createUserUseCase.execute(any()))
                .thenAnswer(call -> new CreateUserOutput(String.valueOf(aExpectedUserId)));

        var json = """
                {
                    "customer_id": "%s",
                    "first_name": "%s",
                    "last_name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(aCustomerId, aFirstName, aLastName, aEmail, aPassword);

        final var aRequest = MockMvcRequestBuilders.post("/v1/users")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/users/" + aExpectedUserId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId.toString()));

        Mockito.verify(createUserUseCase, Mockito.times(1)).execute(createUserInputCaptor.capture());

        final var aCreateUserInput = createUserInputCaptor.getValue();

        Assertions.assertEquals(aCustomerId, aCreateUserInput.customerId());
        Assertions.assertEquals(aFirstName, aCreateUserInput.firstName());
        Assertions.assertEquals(aLastName, aCreateUserInput.lastName());
        Assertions.assertEquals(aEmail, aCreateUserInput.email());
        Assertions.assertEquals(aPassword, aCreateUserInput.password());
    }

    @Test
    void givenAValidRequest_whenCallCreateUserMfa_thenReturnUserIdAndQrCode() throws Exception {
        final var aType = "totp";
        final var aDeviceName = "device";

        final var aExpectedMfaId = UUID.randomUUID();
        final var aQrCode = "qr_code";

        Mockito.when(createUserMfaUseCase.execute(any()))
                .thenAnswer(call -> new CreateUserMfaOutput(String.valueOf(aExpectedMfaId), aQrCode));

        var json = """
                {
                    "type": "%s",
                    "device_name": "%s"
                }
                """.formatted(aType, aDeviceName);

        final var aRequest = MockMvcRequestBuilders.post("/v1/users/mfa")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedMfaId.toString()))
                .andExpect(jsonPath("$.qr_code_url").value(aQrCode));

        Mockito.verify(createUserMfaUseCase, Mockito.times(1))
                .execute(createUserMfaInputCaptor.capture());

        final var aCreateUserMfaInput = createUserMfaInputCaptor.getValue();

        Assertions.assertEquals(aType, aCreateUserMfaInput.type());
        Assertions.assertEquals(aDeviceName, aCreateUserMfaInput.deviceName());
    }

    @Test
    void givenAValidRequest_whenCallConfirmDevice_thenReturnUserId() throws Exception {
        final var aOtpCode = "123456";
        final var aValidUntil = "2021-12-31T23:59:59Z";

        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.when(confirmUserMfaDeviceUseCase.execute(any()))
                .thenAnswer(call -> new ConfirmUserMfaDeviceOutput(aExpectedUserId));

        var json = """
                {
                    "valid_until": "%s"
                }
                """.formatted(aValidUntil);

        final var aRequest = MockMvcRequestBuilders.post("/v1/users/mfa/device/confirm")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("otp_code", aOtpCode)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId));

        Mockito.verify(confirmUserMfaDeviceUseCase, Mockito.times(1))
                .execute(confirmUserMfaDeviceInputCaptor.capture());

        final var aConfirmUserMfaDeviceInput = confirmUserMfaDeviceInputCaptor.getValue();

        Assertions.assertEquals(aOtpCode, aConfirmUserMfaDeviceInput.code());
        Assertions.assertEquals(aValidUntil, aConfirmUserMfaDeviceInput.validUntil().toString());
    }

    @Test
    void givenAValidRequest_whenCallVerifyMfa_thenCallAuthenticationProvider() throws Exception {
        final var aOtp = "123456";

        final var aRequest = MockMvcRequestBuilders.post("/v1/users/mfa/verify")
                .with(ApiTest.admin())
                .with(csrf())
                .param("otp_code", aOtp)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void givenAValidRequest_whenCallDisableMfa_thenCallDisableMfaUseCase() throws Exception {
        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.when(disableUserMfaUseCase.execute(any()))
                .thenAnswer(call -> new DisableUserMfaOutput(aExpectedUserId));

        final var aRequest = MockMvcRequestBuilders.delete("/v1/users/mfa/disable")
                .with(ApiTest.admin(aExpectedUserId))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId));
    }

    @Test
    void givenAValidRequest_whenCallUpdateUser_thenReturnUserId() throws Exception {
        final var aFirstName = "Johns";
        final var aLastName = "Does";
        final var aEmail = "test.tss@testes.com";

        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.when(updateUserUseCase.execute(any()))
                .thenAnswer(call -> new UpdateUserOutput(aExpectedUserId));

        var json = """
                {
                    "first_name": "%s",
                    "last_name": "%s",
                    "email": "%s"
                }
                """.formatted(aFirstName, aLastName, aEmail);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/users")
                .with(ApiTest.admin(aExpectedUserId))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId));

        Mockito.verify(updateUserUseCase, Mockito.times(1)).execute(updateUserInputCaptor.capture());

        final var aUpdateUserInput = updateUserInputCaptor.getValue();

        Assertions.assertEquals(aExpectedUserId, aUpdateUserInput.id().toString());
        Assertions.assertEquals(aFirstName, aUpdateUserInput.firstName());
        Assertions.assertEquals(aLastName, aUpdateUserInput.lastName());
        Assertions.assertEquals(aEmail, aUpdateUserInput.email());
    }

    @Test
    void givenAValidRequest_whenCallMarkAsDeleteUser_thenShouldBeOk() throws Exception {
        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.doNothing().when(markAsDeleteUserUseCase).execute(any());

        final var aRequest = MockMvcRequestBuilders.delete("/v1/users")
                .with(ApiTest.admin(aExpectedUserId))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Mockito.verify(markAsDeleteUserUseCase, Mockito.times(1))
                .execute(Mockito.any());
    }

    @Test
    void givenAValidRequest_whenCallAddRolesToUser_thenReturnUserId() throws Exception {
        final var aRolesIds = UUID.randomUUID().toString();

        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.when(addRolesToUserUseCase.execute(any()))
                .thenAnswer(call -> new AddRolesToUserOutput(aExpectedUserId));

        var json = """
                {
                    "roles_ids": ["%s"]
                }
                """.formatted(aRolesIds);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/users/{id}/add-roles", aExpectedUserId)
                .with(ApiTest.admin(aExpectedUserId))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId));

        Mockito.verify(addRolesToUserUseCase, Mockito.times(1)).execute(Mockito.any());
    }

    @Test
    void givenAValidRequest_whenCallRemoveRoleFromUser_thenReturnUserId() throws Exception {
        final var aRoleId = UUID.randomUUID().toString();

        final var aExpectedUserId = UUID.randomUUID().toString();

        Mockito.when(removeUserRoleUseCase.execute(any()))
                .thenAnswer(call -> new RemoveUserRoleOutput(aExpectedUserId));

        final var aRequest = MockMvcRequestBuilders.patch("/v1/users/{id}/remove-role/{roleId}", aExpectedUserId, aRoleId)
                .with(ApiTest.admin(aExpectedUserId))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.user_id").value(aExpectedUserId));

        Mockito.verify(removeUserRoleUseCase, Mockito.times(1)).execute(Mockito.any());
    }
}
