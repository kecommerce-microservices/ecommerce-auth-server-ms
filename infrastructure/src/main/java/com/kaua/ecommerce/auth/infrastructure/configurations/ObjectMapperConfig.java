package com.kaua.ecommerce.auth.infrastructure.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.auth.infrastructure.configurations.json.Json;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

@JsonComponent
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Json.mapper();
    }
}
