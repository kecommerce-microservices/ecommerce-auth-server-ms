package com.kaua.ecommerce.auth.infrastructure.configurations;

import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import com.kaua.ecommerce.auth.infrastructure.services.local.RSAKeyLocalGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class KeysServiceConfig {

    @Bean
    @Profile({"development", "test-integration"})
    public KeysService rsaKeyLocalGeneratorService() {
        return new RSAKeyLocalGeneratorService();
    }
}
