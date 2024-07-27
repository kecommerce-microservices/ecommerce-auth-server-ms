package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.users.CreateUserUseCase;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.application.usecases.users.outputs.CreateUserOutput;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = UserRestController.class)
class UserRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @Captor
    private ArgumentCaptor<CreateUserInput> createUserInputCaptor;

    @Test
    void givenAValidRequest_whenCallCreateRole_thenReturnUserId() throws Exception {
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
                .with(ApiTest.TEST_ADMIN_JWT)
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
}
