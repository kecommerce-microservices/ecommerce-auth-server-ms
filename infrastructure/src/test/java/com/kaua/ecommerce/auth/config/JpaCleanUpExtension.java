package com.kaua.ecommerce.auth.config;

import com.kaua.ecommerce.auth.infrastructure.mailtokens.persistence.MailTokenJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.oauth2.clients.persistence.ClientJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.roles.persistence.RoleJpaEntityRepository;
import com.kaua.ecommerce.auth.infrastructure.users.persistence.UserJpaEntityRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class JpaCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(UserJpaEntityRepository.class),
                appContext.getBean(RoleJpaEntityRepository.class),
                appContext.getBean(ClientJpaEntityRepository.class),
                appContext.getBean(MailTokenJpaEntityRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
