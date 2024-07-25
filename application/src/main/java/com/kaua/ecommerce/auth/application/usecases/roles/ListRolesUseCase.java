package com.kaua.ecommerce.auth.application.usecases.roles;

import com.kaua.ecommerce.auth.application.UseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.ListRolesOutput;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;

public abstract class ListRolesUseCase extends UseCase<SearchQuery, Pagination<ListRolesOutput>> {
}
