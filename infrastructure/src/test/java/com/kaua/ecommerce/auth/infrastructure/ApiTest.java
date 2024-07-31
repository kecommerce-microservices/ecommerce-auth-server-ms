package com.kaua.ecommerce.auth.infrastructure;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public interface ApiTest {

    JwtRequestPostProcessor TEST_ADMIN_JWT = jwt()
            .authorities(new SimpleGrantedAuthority("manage-oauth2-clients"));
}
