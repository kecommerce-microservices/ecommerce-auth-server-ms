package com.kaua.ecommerce.auth.infrastructure.rest;

import com.kaua.ecommerce.auth.application.usecases.roles.outputs.CreateRoleOutput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.UpdateRoleOutput;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateRoleRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.UpdateRoleRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetDefaultRolesResponse;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetRoleByIdResponse;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.ListRolesResponse;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role", description = "Role API")
@RequestMapping(value = "/v1/roles")
public interface RoleRestApi {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<CreateRoleOutput> createRole(@RequestBody CreateRoleRequest request);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    Pagination<ListRolesResponse> listRoles(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction,
            @RequestParam(name = "start_date", required = false, defaultValue = "") final String startDate,
            @RequestParam(name = "end_date", required = false, defaultValue = "") final String endDate
    );

    @GetMapping(value = "defaults", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all default roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default roles found successfully"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @ResponseStatus(code = HttpStatus.OK)
    List<GetDefaultRolesResponse> getDefaultRoles();

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get a role by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found successfully"),
            @ApiResponse(responseCode = "404", description = "Role id not found"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<GetRoleByIdResponse> getRoleById(@PathVariable String id);

    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Role id not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    ResponseEntity<UpdateRoleOutput> updateRole(@PathVariable String id, @RequestBody UpdateRoleRequest request);

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Soft delete a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role id not found"),
            @ApiResponse(responseCode = "422", description = "A domain validation error was observed"),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deleteRole(@PathVariable String id);
}
