package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.gateways.CryptographyGateway;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.CreateUserInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class CreateUserUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptographyGateway cryptographyGateway;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultCreateUserUseCase createUserUseCase;

    @Test
    void givenAValidValues_whenCallCreateUserUseCase_thenReturnUserId() {
        final var aCustomerId = IdentifierUtils.generateNewId();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "teste@teste.com";
        final var aPassword = "1234567Ab*";

        final var aInput = new CreateUserInput(
                aCustomerId,
                aFirstName,
                aLastName,
                aEmail,
                aPassword
        );

        Mockito.when(userRepository.existsByEmail(aEmail)).thenReturn(false);
        Mockito.when(cryptographyGateway.encrypt(aPassword)).thenReturn("hashedPassword");
        Mockito.when(roleRepository.getDefaultRoles()).thenReturn(List.of(Fixture.Roles.defaultRole()));
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.createUserUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.userId());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(aEmail);
        Mockito.verify(cryptographyGateway, Mockito.times(1)).encrypt(aPassword);
        Mockito.verify(roleRepository, Mockito.times(1)).getDefaultRoles();
        Mockito.verify(userRepository, Mockito.times(1)).save(argThat(cmd ->
                Objects.equals(cmd.getCustomerId().value().toString(), aCustomerId)
                        && Objects.equals(cmd.getName().firstName(), aFirstName)
                        && Objects.equals(cmd.getName().lastName(), aLastName)
                        && Objects.equals(cmd.getEmail().value(), aEmail)
                        && Objects.equals(cmd.getPassword().value(), "hashedPassword")
                        && cmd.getRoles().size() == 1
        ));
    }

    @Test
    void givenAnExistsEmail_whenCallCreateUserUseCase_thenThrowDomainException() {
        final var aCustomerId = IdentifierUtils.generateNewId();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "teste@teste.com";
        final var aPassword = "1234567Ab*";

        final var expectedErrorMessage = "Email already exists";

        final var aInput = new CreateUserInput(
                aCustomerId,
                aFirstName,
                aLastName,
                aEmail,
                aPassword
        );

        Mockito.when(userRepository.existsByEmail(aEmail)).thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createUserUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenANullInput_whenCallCreateUserUseCase_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultCreateUserUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.createUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }
}
