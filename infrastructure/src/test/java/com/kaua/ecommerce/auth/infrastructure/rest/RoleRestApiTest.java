package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.roles.*;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.CreateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.UpdateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.*;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.auth.infrastructure.ApiTest;
import com.kaua.ecommerce.auth.infrastructure.ControllerTest;
import com.kaua.ecommerce.auth.infrastructure.rest.controllers.RoleRestController;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import com.kaua.ecommerce.lib.domain.validation.Error;
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

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = RoleRestController.class)
class RoleRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateRoleUseCase createRoleUseCase;

    @MockBean
    private UpdateRoleUseCase updateRoleUseCase;

    @MockBean
    private MarkAsDeleteRoleUseCase markAsDeleteRoleUseCase;

    @MockBean
    private GetRoleByIdUseCase getRoleByIdUseCase;

    @MockBean
    private ListRolesUseCase listRolesUseCase;

    @MockBean
    private GetDefaultRolesUseCase getDefaultRolesUseCase;

    @Captor
    private ArgumentCaptor<CreateRoleInput> createRoleInputArgumentCaptor;

    @Captor
    private ArgumentCaptor<UpdateRoleInput> updateRoleInputArgumentCaptor;

    @Test
    void givenAValidInput_whenCallCreateRole_thenReturnRoleId() throws Exception {
        final var aName = "ADMIN";
        final var aDescription = "";
        final var aIsDefault = false;
        final var aExpectedRoleId = UUID.randomUUID();

        Mockito.when(createRoleUseCase.execute(any()))
                .thenAnswer(call -> new CreateRoleOutput(String.valueOf(aExpectedRoleId)));

        var json = """
                {
                    "name": "%s",
                    "description": "%s",
                    "is_default": %s
                }
                """.formatted(aName, aDescription, aIsDefault);

        final var aRequest = MockMvcRequestBuilders.post("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/roles/" + aExpectedRoleId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.role_id").value(aExpectedRoleId.toString()));

        Mockito.verify(createRoleUseCase, Mockito.times(1))
                .execute(createRoleInputArgumentCaptor.capture());

        final var aCreateRoleInput = createRoleInputArgumentCaptor.getValue();

        Assertions.assertEquals(aName, aCreateRoleInput.name());
        Assertions.assertEquals(aDescription, aCreateRoleInput.description());
        Assertions.assertEquals(aIsDefault, aCreateRoleInput.isDefault());
    }

    @Test
    void givenAnInvalidEmptyName_whenCallCreateRole_thenReturnError() throws Exception {
        final var aName = "";
        final var aDescription = "Administrator";
        final var aIsDefault = false;

        final var aExpectedProperty = "name";
        final var aExpectedMessage = "should not be empty";

        Mockito.when(createRoleUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(aExpectedProperty, aExpectedMessage)));

        var json = """
                {
                    "name": "%s",
                    "description": "%s",
                    "is_default": %s
                }
                """.formatted(aName, aDescription, aIsDefault);

        final var aRequest = MockMvcRequestBuilders.post("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("DomainException"))
                .andExpect(jsonPath("$.errors[0].property").value(aExpectedProperty))
                .andExpect(jsonPath("$.errors[0].message").value(aExpectedMessage));

        Mockito.verify(createRoleUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidInput_whenCallUpdateRole_thenReturnUpdatedRole() throws Exception {
        final var aRoleId = UUID.randomUUID();
        final var aName = "ADMIN";
        final var aDescription = "";
        final var aIsDefault = false;

        Mockito.when(updateRoleUseCase.execute(any()))
                .thenAnswer(call -> new UpdateRoleOutput(String.valueOf(aRoleId)));

        var json = """
                {
                    "name": "%s",
                    "description": "%s",
                    "is_default": %s
                }
                """.formatted(aName, aDescription, aIsDefault);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/roles/" + aRoleId)
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.role_id").value(aRoleId.toString()));

        Mockito.verify(updateRoleUseCase, Mockito.times(1))
                .execute(updateRoleInputArgumentCaptor.capture());

        final var aUpdateRoleInput = updateRoleInputArgumentCaptor.getValue();

        Assertions.assertEquals(aRoleId.toString(), aUpdateRoleInput.roleId());
        Assertions.assertEquals(aName, aUpdateRoleInput.name());
        Assertions.assertEquals(aDescription, aUpdateRoleInput.description());
        Assertions.assertEquals(aIsDefault, aUpdateRoleInput.isDefault());
    }

    @Test
    void givenAnInvalidRoleId_whenCallUpdateRole_thenReturnError() throws Exception {
        final var aRoleId = UUID.randomUUID();
        final var aName = "ADMIN";
        final var aDescription = "";
        final var aIsDefault = false;

        final var expectedMessage = "Role with id %s was not found".formatted(aRoleId);

        Mockito.when(updateRoleUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Role.class, aRoleId.toString()).get());

        var json = """
                {
                    "name": "%s",
                    "description": "%s",
                    "is_default": %s
                }
                """.formatted(aName, aDescription, aIsDefault);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/roles/" + aRoleId)
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        Mockito.verify(updateRoleUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidRoleId_whenCallMarkAsDeleteRole_thenReturnNoContent() throws Exception {
        final var aRoleId = UUID.randomUUID();

        final var aRequest = MockMvcRequestBuilders.delete("/v1/roles/" + aRoleId)
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());

        Mockito.verify(markAsDeleteRoleUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidRoleId_whenCallGetRoleById_thenReturnRole() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoleId = aRole.getId();

        Mockito.when(this.getRoleByIdUseCase.execute(aRoleId))
                .thenReturn(new GetRoleByIdOutput(aRole));

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles/" + aRoleId.value().toString())
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(aRoleId.value().toString()))
                .andExpect(jsonPath("$.name").value(aRole.getName().value()))
                .andExpect(jsonPath("$.description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$.is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$.is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$.created_at").value(aRole.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aRole.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.deleted_at").value(aRole.getDeletedAt().orElse(null)));

        Mockito.verify(getRoleByIdUseCase, Mockito.times(1)).execute(aRoleId);
    }

    @Test
    void givenAValidValues_whenCallListRoles_thenReturnRoles() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoles = List.of(
                aRole,
                Fixture.Roles.randomRole()
        );
        final var aPaginationMetadata = new PaginationMetadata(
                1,
                10,
                1,
                2
        );
        final var aPagination = new Pagination<>(aPaginationMetadata, aRoles)
                .map(ListRolesOutput::new);

        Mockito.when(this.listRolesUseCase.execute(any()))
                .thenReturn(aPagination);

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .param("search", "ADMIN")
                .param("page", "1")
                .param("per_page", "10")
                .param("sort", "name")
                .param("direction", "asc")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.current_page").value(aPagination.metadata().currentPage()))
                .andExpect(jsonPath("$.metadata.per_page").value(aPagination.metadata().perPage()))
                .andExpect(jsonPath("$.metadata.total_pages").value(aPagination.metadata().totalPages()))
                .andExpect(jsonPath("$.metadata.total_items").value(aPagination.metadata().totalItems()))
                .andExpect(jsonPath("$.items[0].id").value(aRole.getId().value().toString()))
                .andExpect(jsonPath("$.items[0].name").value(aRole.getName().value()))
                .andExpect(jsonPath("$.items[0].description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$.items[0].is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$.items[0].is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$.items[0].created_at").value(aRole.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].deleted_at").value(aRole.getDeletedAt().orElse(null)));

        Mockito.verify(listRolesUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidValuesWithStartDate_whenCallListRoles_thenReturnRoles() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoles = List.of(
                aRole,
                Fixture.Roles.randomRole()
        );
        final var aPaginationMetadata = new PaginationMetadata(
                1,
                10,
                1,
                2
        );
        final var aPagination = new Pagination<>(aPaginationMetadata, aRoles)
                .map(ListRolesOutput::new);

        Mockito.when(this.listRolesUseCase.execute(any()))
                .thenReturn(aPagination);

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .param("search", "ADMIN")
                .param("page", "1")
                .param("per_page", "10")
                .param("sort", "name")
                .param("direction", "asc")
                .param("start_date", "2021-01-01T00:00:00Z")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.current_page").value(aPagination.metadata().currentPage()))
                .andExpect(jsonPath("$.metadata.per_page").value(aPagination.metadata().perPage()))
                .andExpect(jsonPath("$.metadata.total_pages").value(aPagination.metadata().totalPages()))
                .andExpect(jsonPath("$.metadata.total_items").value(aPagination.metadata().totalItems()))
                .andExpect(jsonPath("$.items[0].id").value(aRole.getId().value().toString()))
                .andExpect(jsonPath("$.items[0].name").value(aRole.getName().value()))
                .andExpect(jsonPath("$.items[0].description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$.items[0].is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$.items[0].is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$.items[0].created_at").value(aRole.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].deleted_at").value(aRole.getDeletedAt().orElse(null)));

        Mockito.verify(listRolesUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidValuesWithEndDate_whenCallListRoles_thenReturnRoles() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoles = List.of(
                aRole,
                Fixture.Roles.randomRole()
        );
        final var aPaginationMetadata = new PaginationMetadata(
                1,
                10,
                1,
                2
        );
        final var aPagination = new Pagination<>(aPaginationMetadata, aRoles)
                .map(ListRolesOutput::new);

        Mockito.when(this.listRolesUseCase.execute(any()))
                .thenReturn(aPagination);

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .param("search", "ADMIN")
                .param("page", "1")
                .param("per_page", "10")
                .param("sort", "name")
                .param("direction", "asc")
                .param("end_date", InstantUtils.now().plus(1, ChronoUnit.HOURS).toString())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.current_page").value(aPagination.metadata().currentPage()))
                .andExpect(jsonPath("$.metadata.per_page").value(aPagination.metadata().perPage()))
                .andExpect(jsonPath("$.metadata.total_pages").value(aPagination.metadata().totalPages()))
                .andExpect(jsonPath("$.metadata.total_items").value(aPagination.metadata().totalItems()))
                .andExpect(jsonPath("$.items[0].id").value(aRole.getId().value().toString()))
                .andExpect(jsonPath("$.items[0].name").value(aRole.getName().value()))
                .andExpect(jsonPath("$.items[0].description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$.items[0].is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$.items[0].is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$.items[0].created_at").value(aRole.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].deleted_at").value(aRole.getDeletedAt().orElse(null)));

        Mockito.verify(listRolesUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidAllValues_whenCallListRoles_thenReturnRoles() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoles = List.of(
                aRole,
                Fixture.Roles.randomRole()
        );
        final var aPaginationMetadata = new PaginationMetadata(
                1,
                10,
                1,
                2
        );
        final var aPagination = new Pagination<>(aPaginationMetadata, aRoles)
                .map(ListRolesOutput::new);

        Mockito.when(this.listRolesUseCase.execute(any()))
                .thenReturn(aPagination);

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles")
                .with(ApiTest.admin())
                .with(csrf())
                .param("search", "ADMIN")
                .param("page", "1")
                .param("per_page", "10")
                .param("sort", "name")
                .param("direction", "asc")
                .param("start_date", "2021-01-01T00:00:00Z")
                .param("end_date", "2021-12-31T23:59:59Z")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.current_page").value(aPagination.metadata().currentPage()))
                .andExpect(jsonPath("$.metadata.per_page").value(aPagination.metadata().perPage()))
                .andExpect(jsonPath("$.metadata.total_pages").value(aPagination.metadata().totalPages()))
                .andExpect(jsonPath("$.metadata.total_items").value(aPagination.metadata().totalItems()))
                .andExpect(jsonPath("$.items[0].id").value(aRole.getId().value().toString()))
                .andExpect(jsonPath("$.items[0].name").value(aRole.getName().value()))
                .andExpect(jsonPath("$.items[0].description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$.items[0].is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$.items[0].is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$.items[0].created_at").value(aRole.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].deleted_at").value(aRole.getDeletedAt().orElse(null)));

        Mockito.verify(listRolesUseCase, Mockito.times(1)).execute(any());
    }

    @Test
    void givenAValidDefaultRoles_whenCallGetDefaultRoles_thenReturnRoles() throws Exception {
        final var aRole = Fixture.Roles.defaultRole();
        final var aRoles = Stream.of(
                aRole,
                Role.create(
                        new RoleName("customer"),
                        new RoleDescription("Customer role"),
                        false
                )
        ).map(GetDefaultRolesOutput::new).toList();

        Mockito.when(this.getDefaultRolesUseCase.execute())
                .thenReturn(aRoles);

        final var aRequest = MockMvcRequestBuilders.get("/v1/roles/defaults")
                .with(ApiTest.admin())
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").value(aRole.getId().value().toString()))
                .andExpect(jsonPath("$[0].name").value(aRole.getName().value()))
                .andExpect(jsonPath("$[0].description").value(aRole.getDescription().value()))
                .andExpect(jsonPath("$[0].is_default").value(aRole.isDefault()))
                .andExpect(jsonPath("$[0].is_deleted").value(aRole.isDeleted()))
                .andExpect(jsonPath("$[0].created_at").value(aRole.getCreatedAt().toString()));

        Mockito.verify(getDefaultRolesUseCase, Mockito.times(1)).execute();
    }
}
