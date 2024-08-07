package com.kaua.ecommerce.auth.application.repositories;

import com.kaua.ecommerce.auth.domain.mailtokens.MailToken;

import java.util.List;

public interface MailRepository {

    MailToken save(MailToken mailToken);

    List<MailToken> findByEmail(String email);

    void deleteByToken(String token);
}
