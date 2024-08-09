package com.kaua.ecommerce.auth.infrastructure.users;

import com.kaua.ecommerce.auth.domain.Fixture;
import com.kaua.ecommerce.auth.domain.users.*;
import com.kaua.ecommerce.auth.infrastructure.AbstractCacheTest;
import com.kaua.ecommerce.auth.infrastructure.DatabaseRepositoryTest;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntity;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.temporal.ChronoUnit;
import java.util.Set;

@DatabaseRepositoryTest
class UserRepositoryImplTest extends AbstractCacheTest {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private UserJpaEntityRepository userJpaEntityRepository;

    @Autowired
    private RoleJpaEntityRepository roleJpaEntityRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void givenAValidValues_whenCallSaveUser_thenUserIsSaved() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("teste@tess.com");
        final var aPassword = new UserPassword("123456Ab*");
        final var aRoles = Set.of(aDefaultRole.getId());

        final var aUser = User.newUser(
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles
        );

        Assertions.assertEquals(0, this.userJpaEntityRepository.count());

        final var aOutput = this.userRepositoryImpl.save(aUser);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        final var aUserEntity = this.userJpaEntityRepository.findAll().get(0);

        Assertions.assertEquals(aUser.getId().value(), aUserEntity.getId());
        Assertions.assertEquals(aUser.getCustomerId().value(), aUserEntity.getCustomerId());
        Assertions.assertEquals(aUser.getName().firstName(), aUserEntity.getFirstName());
        Assertions.assertEquals(aUser.getName().lastName(), aUserEntity.getLastName());
        Assertions.assertEquals(aUser.getEmail().value(), aUserEntity.getEmail());
        Assertions.assertEquals(aUser.getPassword().value(), aUserEntity.getPassword());
        Assertions.assertEquals(aUser.isDeleted(), aUserEntity.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aUserEntity.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aUserEntity.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aUserEntity.getUpdatedAt());
        Assertions.assertNull(aUserEntity.getDeletedAt());
    }

    @Test
    void givenAnExistsEmail_whenCallExistsByEmail_thenReturnTrue() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewId());
        final var aName = new UserName("John", "Doe");
        final var aEmail = new UserEmail("teste@tess.com");
        final var aPassword = new UserPassword("123456Ab*");
        final var aRoles = Set.of(aDefaultRole.getId());

        final var aUser = User.newUser(
                aCustomerId,
                aName,
                aEmail,
                aPassword,
                aRoles
        );

        this.userRepositoryImpl.save(aUser);

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        Assertions.assertTrue(this.userRepositoryImpl.existsByEmail(aEmail.value()));
    }

    @Test
    void givenAValidUserId_whenCallFindById_thenReturnUser() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        final var aOutput = this.userRepositoryImpl.findById(aUser.getId().value()).get();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidValues_whenCallUpdateUser_thenUserIsUpdated() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        aUser.getMfa().confirmDevice(InstantUtils.now().plus(30, ChronoUnit.MINUTES));

        final var aOutput = this.userRepositoryImpl.update(aUser);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidEmail_whenCallFindByEmail_thenReturnUser() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        final var aOutput = this.userRepositoryImpl.findByEmail(aUser.getEmail().value()).get();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidUserIdButExistsInCache_whenCallFindById_thenReturnUser() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        this.userRepositoryImpl.findById(aUser.getId().value());

        final var aOutput = this.userRepositoryImpl.findById(aUser.getId().value()).get();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidEmailButExistsInCache_whenCallFindByEmail_thenReturnUser() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        this.userRepositoryImpl.findByEmail(aUser.getEmail().value());

        final var aOutput = this.userRepositoryImpl.findByEmail(aUser.getEmail().value()).get();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }

    @Test
    void givenAValidEmailButIdNotExistsInCache_whenCallFindByEmail_thenReturnEmpty() {
        final var aDefaultRole = Fixture.Roles.defaultRole();
        this.roleJpaEntityRepository.saveAndFlush(RoleJpaEntity.toEntity(aDefaultRole));

        final var aUser = Fixture.Users.randomUser(aDefaultRole.getId());

        this.userJpaEntityRepository.saveAndFlush(UserJpaEntity.toEntity(aUser));

        Assertions.assertEquals(1, this.userJpaEntityRepository.count());

        this.userRepositoryImpl.findByEmail(aUser.getEmail().value());

        this.redisTemplate.delete("users:".concat(aUser.getId().value().toString()));

        final var aOutput = this.userRepositoryImpl.findByEmail(aUser.getEmail().value()).get();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aUser.getId().value(), aOutput.getId().value());
        Assertions.assertEquals(aUser.getCustomerId().value(), aOutput.getCustomerId().value());
        Assertions.assertEquals(aUser.getName().firstName(), aOutput.getName().firstName());
        Assertions.assertEquals(aUser.getName().lastName(), aOutput.getName().lastName());
        Assertions.assertEquals(aUser.getEmail().value(), aOutput.getEmail().value());
        Assertions.assertEquals(aUser.getPassword().value(), aOutput.getPassword().value());
        Assertions.assertEquals(aUser.isDeleted(), aOutput.isDeleted());
        Assertions.assertEquals(aUser.isEmailVerified(), aOutput.isEmailVerified());
        Assertions.assertEquals(aUser.getCreatedAt(), aOutput.getCreatedAt());
        Assertions.assertEquals(aUser.getUpdatedAt(), aOutput.getUpdatedAt());
        Assertions.assertTrue(aOutput.getDeletedAt().isEmpty());
    }
}
