package com.kaua.ecommerce.auth.infrastructure.rest.controllers;

import com.kaua.ecommerce.auth.application.usecases.roles.*;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.CreateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.inputs.UpdateRoleInput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.CreateRoleOutput;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.UpdateRoleOutput;
import com.kaua.ecommerce.auth.domain.roles.RoleId;
import com.kaua.ecommerce.auth.infrastructure.rest.RoleRestApi;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.CreateRoleRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.req.UpdateRoleRequest;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetDefaultRolesResponse;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.GetRoleByIdResponse;
import com.kaua.ecommerce.auth.infrastructure.rest.models.res.ListRolesResponse;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.domain.utils.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class RoleRestController implements RoleRestApi {

    private static final Logger log = LoggerFactory.getLogger(RoleRestController.class);

    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final MarkAsDeleteRoleUseCase markAsDeleteRoleUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final GetDefaultRolesUseCase getDefaultRolesUseCase;

    public RoleRestController(
            final CreateRoleUseCase createRoleUseCase,
            final UpdateRoleUseCase updateRoleUseCase,
            final MarkAsDeleteRoleUseCase markAsDeleteRoleUseCase,
            final GetRoleByIdUseCase getRoleByIdUseCase,
            final ListRolesUseCase listRolesUseCase,
            final GetDefaultRolesUseCase getDefaultRolesUseCase
    ) {
        this.createRoleUseCase = Objects.requireNonNull(createRoleUseCase);
        this.updateRoleUseCase = Objects.requireNonNull(updateRoleUseCase);
        this.markAsDeleteRoleUseCase = Objects.requireNonNull(markAsDeleteRoleUseCase);
        this.getRoleByIdUseCase = Objects.requireNonNull(getRoleByIdUseCase);
        this.listRolesUseCase = Objects.requireNonNull(listRolesUseCase);
        this.getDefaultRolesUseCase = Objects.requireNonNull(getDefaultRolesUseCase);
    }

    @Override
    public ResponseEntity<CreateRoleOutput> createRole(final CreateRoleRequest request) {
        log.debug("Creating a new role: {}", request);

        final var aInput = new CreateRoleInput(
                request.name(),
                request.description(),
                request.isDefault()
        );

        final var aOutput = this.createRoleUseCase.execute(aInput);

        log.info("Role created successfully: {}", aOutput);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/v1/roles/" + aOutput.roleId()))
                .body(aOutput);
    }

    @Override
    public Pagination<ListRolesResponse> listRoles(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction,
            final String startDate,
            final String endDate
    ) {
        log.debug("Listing roles: search={}, page={}, perPage={}, sort={}, direction={}, startDate={}, endDate={}",
                search, page, perPage, sort, direction, startDate, endDate);

        return this.listRolesUseCase.execute(new SearchQuery(
                page,
                perPage,
                search,
                sort,
                direction,
                createPeriod(startDate, endDate)
        )).map(ListRolesResponse::new);
    }

    @Override
    public List<GetDefaultRolesResponse> getDefaultRoles() {
        log.debug("Getting default roles");
        return this.getDefaultRolesUseCase.execute()
                .stream().map(GetDefaultRolesResponse::new)
                .toList();
    }

    @Override
    public ResponseEntity<GetRoleByIdResponse> getRoleById(final String id) {
        log.debug("Getting role by id: {}", id);
        return ResponseEntity.ok(new GetRoleByIdResponse(
                this.getRoleByIdUseCase.execute(new RoleId(id))));
    }

    @Override
    public ResponseEntity<UpdateRoleOutput> updateRole(final String id, final UpdateRoleRequest request) {
        log.debug("Updating role: {}", request);

        final var aInput = new UpdateRoleInput(
                id,
                request.name(),
                request.description(),
                request.isDefault()
        );

        final var aOutput = this.updateRoleUseCase.execute(aInput);

        log.info("Role updated successfully: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }

    @Override
    public void deleteRole(final String id) {
        log.debug("Soft deleting role: {}", id);
        this.markAsDeleteRoleUseCase.execute(new RoleId(UUID.fromString(id)));
        log.info("Role deleted successfully: {}", id);
    }

    private Period createPeriod(final String startDate, final String endDate) {
        if (startDate.isBlank() && endDate.isBlank()) {
            return null;
        }

        final var aInstantStart = Period.startValidate(
                startDate,
                10,
                ChronoUnit.DAYS
        );
        final var aInstantEnd = Period.endValidate(
                endDate,
                10,
                ChronoUnit.DAYS
        );
        return new Period(aInstantStart, aInstantEnd);
    }
}
