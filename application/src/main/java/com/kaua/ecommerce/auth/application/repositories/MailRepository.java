package com.kaua.ecommerce.auth.application.repositories;

import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;

import java.util.List;
import java.util.Optional;

public interface MailRepository {

    MailToken save(MailToken mailToken);

    MailToken update(MailToken mailToken);

    List<MailToken> findByEmail(String email);

    Optional<MailToken> findByToken(String token);

    void deleteByToken(String token);
}
