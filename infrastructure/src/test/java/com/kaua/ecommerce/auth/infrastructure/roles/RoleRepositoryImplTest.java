package com.kaua.ecommerce.auth.infrastructure.roles;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.roles.Role;
import com.kaua.ecommerce.auth.domain.roles.RoleDescription;
import com.kaua.ecommerce.auth.domain.roles.RoleName;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import com.kaua.ecommerce.lib.domain.utils.Period;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;
import java.util.List;

@DatabaseRepositoryTest
class RoleRepositoryImplTest {

    @Autowired
    private RoleRepositoryImpl roleRepositoryImpl;

    @Autowired
    private RoleJpaEntityRepository roleJpaEntityRepository;

    @Test
    void givenAValidRole_whenCallSave_thenShouldSaveRole() {
        final var aName = new RoleName("ADMINISTRATOR");
        final var aDescription = new RoleDescription("Administrator role");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        final var aRoleSaved = this.roleRepositoryImpl.save(aRole);

        Assertions.assertEquals(1, roleJpaEntityRepository.count());

        Assertions.assertEquals(aRole.getId().value(), aRoleSaved.getId().value());
        Assertions.assertEquals(aRole.getName().value(), aRoleSaved.getName().value());
        Assertions.assertEquals(aRole.getDescription().value(), aRoleSaved.getDescription().value());
        Assertions.assertEquals(aRole.isDefault(), aRoleSaved.isDefault());
        Assertions.assertFalse(aRoleSaved.isDeleted());
        Assertions.assertEquals(aRole.getCreatedAt(), aRoleSaved.getCreatedAt());
        Assertions.assertEquals(aRole.getUpdatedAt(), aRoleSaved.getUpdatedAt());
        Assertions.assertTrue(aRoleSaved.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidRole_whenCallExistsByName_thenShouldReturnTrue() {
        final var aName = new RoleName("ADMINISTRATOR");
        final var aDescription = new RoleDescription("Administrator role");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        final var aRoleSaved = this.roleRepositoryImpl.save(aRole);

        final var existsByName = this.roleRepositoryImpl.existsByName(aRoleSaved.getName().value());

        Assertions.assertTrue(existsByName);
    }

    @Test
    void givenAValidValues_whenCallUpdate_thenShouldUpdateRole() {
        final var aRole = Fixture.Roles.defaultRole();

        this.roleRepositoryImpl.save(aRole);

        final var aRoleUpdatedAt = aRole.getUpdatedAt();

        final var aRoleWithNewValues = aRole.update(
                new RoleName("USER"),
                new RoleDescription("User role"),
                true
        );
        final var aRoleUpdated = this.roleRepositoryImpl.update(aRoleWithNewValues);

        Assertions.assertEquals(aRole.getId().value(), aRoleUpdated.getId().value());
        Assertions.assertEquals(aRoleWithNewValues.getName().value(), aRoleUpdated.getName().value());
        Assertions.assertEquals(aRoleWithNewValues.getDescription().value(), aRoleUpdated.getDescription().value());
        Assertions.assertEquals(aRoleWithNewValues.isDefault(), aRoleUpdated.isDefault());
        Assertions.assertFalse(aRoleUpdated.isDeleted());
        Assertions.assertEquals(aRole.getCreatedAt(), aRoleUpdated.getCreatedAt());
        Assertions.assertTrue(aRoleUpdatedAt.isBefore(aRoleUpdated.getUpdatedAt()));
        Assertions.assertTrue(aRoleUpdated.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidId_whenCallFindById_thenShouldReturnRole() {
        final var aName = new RoleName("ADMINISTRATOR");
        final var aDescription = new RoleDescription("Administrator role");
        final var aIsDefault = false;

        final var aRole = Role.create(aName, aDescription, aIsDefault);

        final var aRoleSaved = this.roleRepositoryImpl.save(aRole);

        final var aRoleFound = this.roleRepositoryImpl.findById(aRoleSaved.getId().value());

        Assertions.assertTrue(aRoleFound.isPresent());
        Assertions.assertEquals(aRoleSaved.getId().value(), aRoleFound.get().getId().value());
        Assertions.assertEquals(aRoleSaved.getName().value(), aRoleFound.get().getName().value());
        Assertions.assertEquals(aRoleSaved.getDescription().value(), aRoleFound.get().getDescription().value());
        Assertions.assertEquals(aRoleSaved.isDefault(), aRoleFound.get().isDefault());
        Assertions.assertFalse(aRoleFound.get().isDeleted());
        Assertions.assertEquals(aRoleSaved.getCreatedAt(), aRoleFound.get().getCreatedAt());
        Assertions.assertEquals(aRoleSaved.getUpdatedAt(), aRoleFound.get().getUpdatedAt());
        Assertions.assertTrue(aRoleFound.get().getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidTwoRolesDefault_whenCallCountIsDefaultRoles_thenShouldReturnTwo() {
        final var aRole1 = Role.create(
                new RoleName("CUSTOMER"),
                new RoleDescription("Customer role"),
                true
        );
        final var aRole2 = Role.create(
                new RoleName("USER"),
                new RoleDescription("User role"),
                true
        );
        final var aRole3 = Role.create(
                new RoleName("MANAGER"),
                new RoleDescription("Manager role"),
                false
        );

        this.roleRepositoryImpl.save(aRole1);
        this.roleRepositoryImpl.save(aRole2);
        this.roleRepositoryImpl.save(aRole3);

        final var countIsDefaultRoles = this.roleRepositoryImpl.countIsDefaultRoles();

        Assertions.assertEquals(2, countIsDefaultRoles);
        Assertions.assertEquals(3, roleJpaEntityRepository.count());
    }

    @Test
    void givenPrePersistedRoles_whenCallFindAll_shouldReturnPaginated() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalItems = 3;
        final var aTotalPages = 3;

        final var aRoleUser = Role.create(
                new RoleName("User"),
                new RoleDescription(""),
                true
        );
        final var aRoleAdmin = Role.create(
                new RoleName("Admin"),
                new RoleDescription("Admin user"),
                false
        );
        final var aRoleCeo = Role.create(
                new RoleName("CEO"),
                new RoleDescription("Chief Executive Officer"),
                false
        );

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        roleJpaEntityRepository.saveAllAndFlush(List.of(
                RoleJpaEntity.toEntity(aRoleUser),
                RoleJpaEntity.toEntity(aRoleAdmin),
                RoleJpaEntity.toEntity(aRoleCeo)
        ));

        Assertions.assertEquals(3, roleJpaEntityRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        final var aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleAdmin.getId(), aResult.items().get(0).getId());
    }

    @Test
    void givenEmptyRolesTable_whenCallFindAll_shouldReturnEmptyPage() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalItems = 0;
        final var aTotalPages = 0;

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        final var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        final var aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(0, aResult.items().size());
    }

    @Test
    void givenFollowPagination_whenCallFindAllWithPageOne_shouldReturnPaginated() {
        var aPage = 0;
        final var aPerPage = 1;
        final var aTotalItems = 3;
        final var aTotalPages = 3;

        final var aRoleUser = Role.create(
                new RoleName("User"),
                new RoleDescription(""),
                true
        );
        final var aRoleAdmin = Role.create(
                new RoleName("Admin"),
                new RoleDescription("Admin user"),
                false
        );
        final var aRoleCeo = Role.create(
                new RoleName("CEO"),
                new RoleDescription("Chief Executive Officer"),
                false
        );

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        roleJpaEntityRepository.saveAllAndFlush(List.of(
                RoleJpaEntity.toEntity(aRoleUser),
                RoleJpaEntity.toEntity(aRoleAdmin),
                RoleJpaEntity.toEntity(aRoleCeo)
        ));

        Assertions.assertEquals(3, roleJpaEntityRepository.count());

        // Page 0
        var aQuery = new SearchQuery(0, 1, "", "name", "asc");
        var aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleAdmin.getId(), aResult.items().get(0).getId());

        // Page 1
        aPage = 1;

        aQuery = new SearchQuery(1, 1, "", "name", "asc");
        aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleCeo.getId(), aResult.items().get(0).getId());

        // Page 2
        aPage = 2;

        aQuery = new SearchQuery(2, 1, "", "name", "asc");
        aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleUser.getId(), aResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedRolesAndCeAsTerm_whenCallFindAll_shouldReturnPaginated() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalItems = 1;
        final var aTotalPages = 1;

        final var aRoleUser = Role.create(
                new RoleName("User"),
                new RoleDescription(""),
                true
        );
        final var aRoleAdmin = Role.create(
                new RoleName("Admin"),
                new RoleDescription("Admin user"),
                false
        );
        final var aRoleCeo = Role.create(
                new RoleName("CEO"),
                new RoleDescription("Chief Executive Officer"),
                false
        );

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        roleJpaEntityRepository.saveAllAndFlush(List.of(
                RoleJpaEntity.toEntity(aRoleUser),
                RoleJpaEntity.toEntity(aRoleAdmin),
                RoleJpaEntity.toEntity(aRoleCeo)
        ));

        Assertions.assertEquals(3, roleJpaEntityRepository.count());

        final var aQuery = new SearchQuery(0, 1, "ce", "name", "asc");
        final var aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleCeo.getId(), aResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedRolesAndPeriod_whenCallFindAll_shouldReturnPaginated() {
        final var aPage = 0;
        final var aPerPage = 1;
        final var aTotalItems = 1;
        final var aTotalPages = 1;

        final var aRoleUser = Role.create(
                new RoleName("User"),
                new RoleDescription(""),
                true
        );
        final var aRoleAdmin = Role.create(
                new RoleName("Admin"),
                new RoleDescription("Admin user"),
                false
        );
        final var aRoleCeo = Role.create(
                new RoleName("CEO"),
                new RoleDescription("Chief Executive Officer"),
                false
        );

        final var aRoleAdminEntity = RoleJpaEntity.toEntity(aRoleAdmin);
        aRoleAdminEntity.setDeleted(true);
        aRoleAdminEntity.setDeletedAt(InstantUtils.now().plus(10, ChronoUnit.MINUTES));

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        roleJpaEntityRepository.saveAllAndFlush(List.of(
                RoleJpaEntity.toEntity(aRoleUser),
                aRoleAdminEntity,
                RoleJpaEntity.toEntity(aRoleCeo)
        ));

        Assertions.assertEquals(3, roleJpaEntityRepository.count());

        final var aPeriod = new Period(
                InstantUtils.now().minus(5, ChronoUnit.MINUTES),
                InstantUtils.now().plus(1, ChronoUnit.HOURS)
        );
        final var aQuery = new SearchQuery(0, 1, "", "name", "asc", aPeriod);
        final var aResult = roleRepositoryImpl.findAll(aQuery);

        Assertions.assertEquals(aPage, aResult.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aResult.metadata().perPage());
        Assertions.assertEquals(aTotalItems, aResult.metadata().totalItems());
        Assertions.assertEquals(aTotalPages, aResult.metadata().totalPages());
        Assertions.assertEquals(aPerPage, aResult.items().size());

        Assertions.assertEquals(aRoleAdmin.getId(), aResult.items().get(0).getId());
    }

    @Test
    void givenOneDefaultRoleAndOneDeletedRole_whenCallGetDefaultRoles_shouldReturnOneDefaultRole() {
        final var aRoleUser = Role.create(
                new RoleName("User"),
                new RoleDescription(""),
                true
        );
        final var aRoleCeo = Role.create(
                new RoleName("CEO"),
                new RoleDescription("Chief Executive Officer"),
                false
        );
        final var aRoleAdmin = Role.create(
                new RoleName("Admin"),
                new RoleDescription("Admin user"),
                false
        ).markAsDeleted();

        Assertions.assertEquals(0, roleJpaEntityRepository.count());

        roleJpaEntityRepository.saveAllAndFlush(List.of(
                RoleJpaEntity.toEntity(aRoleUser),
                RoleJpaEntity.toEntity(aRoleCeo),
                RoleJpaEntity.toEntity(aRoleAdmin)
        ));

        Assertions.assertEquals(3, roleJpaEntityRepository.count());

        final var aResult = roleRepositoryImpl.getDefaultRoles();

        Assertions.assertEquals(1, aResult.size());
        Assertions.assertEquals(aRoleUser.getId(), aResult.get(0).getId());
    }
}
