package com.kaua.ecommerce.auth.infrastructure.roles;

import com.kaua.ecommerce.auth.application.repositories.RoleRepository;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.utils.SpecificationUtils;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.domain.utils.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoleRepositoryImpl implements RoleRepository {

    private static Logger log = LoggerFactory.getLogger(RoleRepositoryImpl.class);

    private final RoleJpaEntityRepository roleJpaEntityRepository;

    public RoleRepositoryImpl(final RoleJpaEntityRepository roleJpaEntityRepository) {
        this.roleJpaEntityRepository = Objects.requireNonNull(roleJpaEntityRepository);
    }

    @Override
    public Role save(final Role role) {
        log.debug("Saving role: {}", role);
        final var aRoleSaved = this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(role))
                .toDomain();
        log.info("Role saved: {}", aRoleSaved);
        return aRoleSaved;
    }

    @Override
    public Role update(final Role role) {
        log.debug("Updating role: {}", role);
        final var aRoleUpdated = this.roleJpaEntityRepository.save(RoleJpaEntity.toEntity(role))
                .toDomain();
        log.info("Role updated: {}", aRoleUpdated);
        return aRoleUpdated;
    }

    @Override
    public boolean existsByName(final String name) {
        return this.roleJpaEntityRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Role> findById(final UUID id) {
        return this.roleJpaEntityRepository.findById(id)
                .map(RoleJpaEntity::toDomain);
    }

    @Override
    public int countIsDefaultRoles() {
        return this.roleJpaEntityRepository.countByIsDefaultTrue();
    }

    @Override
    public Pagination<Role> findAll(final SearchQuery query) {
        final var aPageRequest = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var aSpecificationTerms = Optional.ofNullable(query.terms())
                .filter(term -> !term.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var aSpecificationBetween = Optional.ofNullable(query.period())
                .map(this::assembleSpecificationBetween)
                .orElse(null);

        final var aSpecification = Specification.where(aSpecificationTerms)
                .and(aSpecificationBetween);

        final var aPage = this.roleJpaEntityRepository.findAll(aSpecification, aPageRequest);

        final var aMetadata = new PaginationMetadata(
                aPage.getNumber(),
                aPage.getSize(),
                aPage.getTotalPages(),
                aPage.getTotalElements()
        );
        return new Pagination<>(
                aMetadata,
                aPage.map(RoleJpaEntity::toDomain).stream().toList()
        );
    }

    @Override
    public List<Role> getDefaultRoles() {
        return this.roleJpaEntityRepository.findAllByIsDefaultTrueAndIsDeletedFalse()
                .stream().map(RoleJpaEntity::toDomain)
                .toList();
    }

    private Specification<RoleJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.<RoleJpaEntity>like("name", terms)
                .or(SpecificationUtils.like("description", terms));
    }

    private Specification<RoleJpaEntity> assembleSpecificationBetween(final Period period) {
        return SpecificationUtils.<RoleJpaEntity>isTrue("isDeleted")
                .and(SpecificationUtils.between("deletedAt", period.start(), period.end()));
    }
}
