package com.kaua.ecommerce.auth.application.repositories;

import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository {

    Role save(Role role);

    Role update(Role role);

    boolean existsByName(String name);

    Optional<Role> findById(UUID id);

    int countIsDefaultRoles();

    Pagination<Role> findAll(SearchQuery query);

    List<Role> getDefaultRoles();

    Set<Role> findByIds(Set<UUID> ids);
}