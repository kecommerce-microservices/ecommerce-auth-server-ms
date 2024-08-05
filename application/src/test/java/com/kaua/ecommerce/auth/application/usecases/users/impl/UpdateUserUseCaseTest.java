package com.kaua.ecommerce.auth.application.usecases.users.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.application.usecases.users.inputs.UpdateUserInput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.exceptions.UserIsDeletedException;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class UpdateUserUseCaseTest extends UseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUpdateUserUseCase updateUserUseCase;

    @Test
    void givenAValidValues_whenCallUpdateUserUseCase_thenShouldUpdateUser() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);

        final var aUserId = aUser.getId().value();
        final var aNewFirstName = "New First Name";
        final var aNewLastName = "New Last Name";
        final var aNewEmail = "tss.tess@tss.com";

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        Mockito.when(userRepository.existsByEmail(aNewEmail)).thenReturn(false);
        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateUserUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUserId.toString());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(aNewEmail);
        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId(), aUser.getId())
                        && Objects.equals(cmd.getName().firstName(), aNewFirstName)
                        && Objects.equals(cmd.getName().lastName(), aNewLastName)
                        && Objects.equals(cmd.getEmail().value(), aNewEmail))
        );
    }

    @Test
    void givenAEmailAlreadyExists_whenCallUpdateUserUseCase_thenShouldThrowDomainException() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);

        final var aUserId = aUser.getId().value();
        final var aNewFirstName = "New First Name";
        final var aNewLastName = "New Last Name";
        final var aNewEmail = "testt.com@gmail.com";

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        final var expectedErrorMessage = "Email already exists";

        Mockito.when(userRepository.existsByEmail(aNewEmail)).thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.updateUserUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(aNewEmail);
        Mockito.verify(userRepository, Mockito.never()).findById(aUserId);
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallUpdateUserUseCase_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateUserUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.updateUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.never()).existsByEmail(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }

    @Test
    void givenANullFirstName_whenCallUpdateUserUseCase_thenShouldNotUpdateName() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);

        final var aUserId = aUser.getId().value();
        final String aNewFirstName = null;
        final var aNewLastName = "New Last Name";
        final var aNewEmail = "test.sss@gmail.com";

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        Mockito.when(userRepository.existsByEmail(aNewEmail)).thenReturn(false);
        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateUserUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUserId.toString());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(aNewEmail);
        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId(), aUser.getId())
                        && Objects.equals(cmd.getName().firstName(), aUser.getName().firstName())
                        && Objects.equals(cmd.getName().lastName(), aUser.getName().lastName())
                        && Objects.equals(cmd.getEmail().value(), aNewEmail))
        );
    }

    @Test
    void givenANullLastName_whenCallUpdateUserUseCase_thenShouldNotUpdateName() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);

        final var aUserId = aUser.getId().value();
        final var aNewFirstName = "New First Name";
        final String aNewLastName = null;
        final var aNewEmail = "test.com@gmail.com";

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        Mockito.when(userRepository.existsByEmail(aNewEmail)).thenReturn(false);
        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateUserUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUserId.toString());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(aNewEmail);
        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId().value(), aUser.getId().value())
                        && Objects.equals(cmd.getName().firstName(), aUser.getName().firstName())
                        && Objects.equals(cmd.getName().lastName(), aUser.getName().lastName())
                        && Objects.equals(cmd.getEmail().value(), aNewEmail))
        );
    }

    @Test
    void givenANullEmail_whenCallUpdateUserUseCase_thenShouldNotUpdateEmail() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);

        final var aUserId = aUser.getId().value();
        final var aNewFirstName = "New First Name";
        final var aNewLastName = "New Last Name";
        final String aNewEmail = null;

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));
        Mockito.when(userRepository.update(aUser)).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateUserUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOutput.userId(), aUserId.toString());

        Mockito.verify(userRepository, Mockito.times(0)).existsByEmail(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.times(1)).update(argThat(cmd ->
                Objects.equals(cmd.getId(), aUser.getId())
                        && Objects.equals(cmd.getName().firstName(), aNewFirstName)
                        && Objects.equals(cmd.getName().lastName(), aNewLastName)
                        && Objects.equals(cmd.getEmail().value(), aUser.getEmail().value()))
        );
    }

    @Test
    void givenAnDeletedUser_whenCallUpdateUserUseCase_thenShouldThrowUserIsDeletedException() {
        final var aRoleId = new RoleId(IdentifierUtils.generateNewId());
        final var aUser = Fixture.Users.randomUser(aRoleId);
        aUser.markAsDeleted();

        final var aUserId = aUser.getId().value();
        final var aNewFirstName = "New First Name";
        final var aNewLastName = "New Last Name";
        final var aNewEmail = "testes.tess@gmail.com";

        final var aInput = new UpdateUserInput(aUserId, aNewFirstName, aNewLastName, aNewEmail);

        final var expectedErrorMessage = "User with id %s is deleted".formatted(aUserId);

        Mockito.when(userRepository.findById(aUserId)).thenReturn(Optional.of(aUser));

        final var aException = Assertions.assertThrows(UserIsDeletedException.class,
                () -> this.updateUserUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(aUserId);
        Mockito.verify(userRepository, Mockito.never()).update(Mockito.any());
    }
}
