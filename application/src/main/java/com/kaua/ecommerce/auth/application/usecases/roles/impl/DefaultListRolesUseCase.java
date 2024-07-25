package com.kaua.ecommerce.auth.application.usecases.roles.impl;

import com.kaua.ecommerce.auth.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.application.usecases.roles.ListRolesUseCase;
import com.kaua.ecommerce.auth.application.usecases.roles.outputs.ListRolesOutput;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListRolesUseCase extends ListRolesUseCase {

    private final RoleRepository roleRepository;

    public DefaultListRolesUseCase(final RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public Pagination<ListRolesOutput> execute(final SearchQuery query) {
        if (query == null) throw new UseCaseInputCannotBeNullException(DefaultListRolesUseCase.class.getSimpleName());

        return this.roleRepository.findAll(query)
                .map(ListRolesOutput::new);
    }
}
