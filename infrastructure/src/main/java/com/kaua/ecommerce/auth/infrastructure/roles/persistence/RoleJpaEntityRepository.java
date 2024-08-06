package com.kaua.ecommerce.auth.infrastructure.roles.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleJpaEntityRepository extends JpaRepository<RoleJpaEntity, UUID> {

    boolean existsByName(String name);

    int countByIsDefaultTrueAndIsDeletedFalse();

    Page<RoleJpaEntity> findAll(Specification<RoleJpaEntity> whereClause, Pageable pageable);

    List<RoleJpaEntity> findAllByIsDefaultTrueAndIsDeletedFalse();

    Set<RoleJpaEntity> findAllByIdInAndIsDeletedFalse(Set<UUID> ids);
}
