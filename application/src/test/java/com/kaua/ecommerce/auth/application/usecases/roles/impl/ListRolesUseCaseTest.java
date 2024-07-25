package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.UseCaseTest;
import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.ListRolesOutput;
import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

class ListRolesUseCaseTest extends UseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DefaultListRolesUseCase listRolesUseCase;

    @Test
    void givenAValidQuery_whenCallListRolesUseCase_shouldReturnARoles() {
        final var aDefaultRole = Fixture.Roles.defaultRole();

        final var aRoles = List.of(
                aDefaultRole,
                Fixture.Roles.randomRole());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aMetadata = new PaginationMetadata(aPage, aPerPage, aTotalPages, aRoles.size());
        final var aPagination = new Pagination<>(aMetadata, aRoles);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListRolesOutput::new);

        Mockito.when(this.roleRepository.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.listRolesUseCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.metadata().totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, actualResult.metadata().perPage());
        Assertions.assertEquals(aTotalPages, actualResult.metadata().totalPages());
        Assertions.assertEquals(aRoles.size(), actualResult.items().size());
    }

    @Test
    void givenAValidQueryButHasNoData_whenCallListRolesUseCase_shouldReturnEmptyRoles() {
        final var aRoles = List.<Role>of();

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "name";
        final var aDirection = "asc";

        final var aQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);
        final var aMetadata = new PaginationMetadata(aPage, aPerPage, aTotalPages, 0);
        final var aPagination = new Pagination<>(aMetadata, aRoles);

        final var aItemsCount = 0;
        final var aResult = aPagination.map(ListRolesOutput::new);

        Mockito.when(this.roleRepository.findAll(aQuery)).thenReturn(aPagination);

        final var actualResult = this.listRolesUseCase.execute(aQuery);

        Assertions.assertEquals(aItemsCount, actualResult.metadata().totalItems());
        Assertions.assertEquals(aResult, actualResult);
        Assertions.assertEquals(aPage, actualResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, actualResult.metadata().perPage());
        Assertions.assertEquals(aTotalPages, actualResult.metadata().totalPages());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    void givenAnInvalidNullQuery_whenCallListRolesUseCase_shouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultListRolesUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.listRolesUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(this.roleRepository, Mockito.never()).findAll(Mockito.any());
    }
}
