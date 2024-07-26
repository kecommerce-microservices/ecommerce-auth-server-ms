package com.kaua.ecommerce.auth.application.repositories;

import com.kaua.ecommerce.auth.domain.users.User;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);
}