package com.kaua.ecommerce.auth.infrastructure.configurations.initializer;

import com.kaua.ecommerce.auth.infrastructure.constants.Constants;
import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnProperty(
        value = "auth-server.data-keys-initializer.enabled",
        havingValue = "true"
)
@Profile({"!test-integration"})
public class DataKeysInitializer implements CommandLineRunner {

    private final KeysService keysService;

    public DataKeysInitializer(final KeysService keysService) {
        this.keysService = Objects.requireNonNull(keysService);
    }

    @Override
    public void run(String... args) {
        this.keysService.generateAndSaveKeys(
                Constants.MFA_PUBLIC_KEY,
                Constants.MFA_PRIVATE_KEY
        );
        this.keysService.generateAndSaveKeys(
                Constants.JWT_PUBLIC_KEY,
                Constants.JWT_PRIVATE_KEY
        );
    }
}
