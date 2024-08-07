package com.kaua.ecommerce.auth.application.repositories;

import com.kaua.ecommerce.auth.domain.users.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    User update(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}