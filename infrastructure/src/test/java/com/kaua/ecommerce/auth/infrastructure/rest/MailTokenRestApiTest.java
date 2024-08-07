package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.users.CreateMailTokenUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateMailTokenInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateMailTokenOutput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.mailtokens.MailType;
import com.kaua.ecommerce.auth.infrastructure.ApiTest;
import com.kaua.ecommerce.auth.infrastructure.ControllerTest;
import com.kaua.ecommerce.auth.infrastructure.rest.controllers.MailTokenRestController;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = MailTokenRestController.class)
class MailTokenRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateMailTokenUseCase createMailTokenUseCase;

    @Captor
    private ArgumentCaptor<CreateMailTokenInput> createMailTokenInputCaptor;

//    @MockBean
//    private CreateUserUseCase createUserUseCase;
//
//    @MockBean
//    private CreateUserMfaUseCase createUserMfaUseCase;
//
//    @MockBean
//    private ConfirmUserMfaDeviceUseCase confirmUserMfaDeviceUseCase;
//
//    @MockBean
//    private DisableUserMfaUseCase disableUserMfaUseCase;
//
//    @MockBean
//    private UpdateUserUseCase updateUserUseCase;
//
//    @MockBean
//    private MarkAsDeleteUserUseCase markAsDeleteUserUseCase;
//
//    @MockBean
//    private AddRolesToUserUseCase addRolesToUserUseCase;
//
//    @MockBean
//    private RemoveUserRoleUseCase removeUserRoleUseCase;
//
//    @MockBean
//    private GetUserByIdUseCase getUserByIdUseCase;
//
//    @Captor
//    private ArgumentCaptor<CreateUserInput> createUserInputCaptor;
//
//    @Captor
//    private ArgumentCaptor<CreateUserMfaInput> createUserMfaInputCaptor;
//
//    @Captor
//    private ArgumentCaptor<ConfirmUserMfaDeviceInput> confirmUserMfaDeviceInputCaptor;
//
//    @Captor
//    private ArgumentCaptor<UpdateUserInput> updateUserInputCaptor;

    @Test
    void givenAValidRequest_whenCallCreateMailConfirmationToken_thenReturnMailIdAndUserId() throws Exception {
        final var aMail = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.EMAIL_CONFIRMATION
        );

        final var aEmail = aMail.getEmail();

        Mockito.when(createMailTokenUseCase.execute(Mockito.any()))
                .thenAnswer(call -> new CreateMailTokenOutput(aMail));

        var json = """
                {
                    "email": "%s"
                }
                """.formatted(aEmail);

        final var aRequest = MockMvcRequestBuilders.post("/v1/mail-tokens/email-confirmation")
                .with(ApiTest.admin())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        final var aResponse = mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value(aMail.getUserId().value().toString()))
                .andExpect(jsonPath("$.mail_id").value(aMail.getId().value().toString()))
                .andExpect(jsonPath("$.type").value(aMail.getType().name()));

        Mockito.verify(createMailTokenUseCase, Mockito.times(1)).execute(createMailTokenInputCaptor.capture());

        final var aCapturedInput = createMailTokenInputCaptor.getValue();

        Assertions.assertEquals(aEmail, aCapturedInput.email());
    }

    @Test
    void givenAValidRequest_whenCallCreateMailPasswordResetToken_thenReturnMailIdAndUserId() throws Exception {
        final var aMail = Fixture.Mails.mail(
                Fixture.Users.email(),
                IdentifierUtils.generateNewId(),
                MailType.PASSWORD_RESET
        );

        final var aEmail = aMail.getEmail();

        Mockito.when(createMailTokenUseCase.execute(Mockito.any()))
                .thenAnswer(call -> new CreateMailTokenOutput(aMail));

        var json = """
                {
                    "email": "%s"
                }
                """.formatted(aEmail);

        final var aRequest = MockMvcRequestBuilders.post("/v1/mail-tokens/password-reset")
                .with(ApiTest.admin())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        final var aResponse = mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value(aMail.getUserId().value().toString()))
                .andExpect(jsonPath("$.mail_id").value(aMail.getId().value().toString()))
                .andExpect(jsonPath("$.type").value(aMail.getType().name()));

        Mockito.verify(createMailTokenUseCase, Mockito.times(1)).execute(createMailTokenInputCaptor.capture());

        final var aCapturedInput = createMailTokenInputCaptor.getValue();

        Assertions.assertEquals(aEmail, aCapturedInput.email());
    }
}
