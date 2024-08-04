package com.kaua.ecommerce.auth.infrastructure;

import com.kaua.ecommerce.auth.infrastructure.configurations.authentication.EcommerceUserAuthentication;
import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

public interface ApiTest {

    static RequestPostProcessor admin() {
        return admin(IdentifierUtils.generateNewId());
    }

    static RequestPostProcessor admin(final String userId) {
        Jwt.Builder jwtBuilder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, userId);

        return SecurityMockMvcRequestPostProcessors.authentication(new EcommerceUserAuthentication(
                List.of(new SimpleGrantedAuthority("admin")),
                jwtBuilder.build(),
                new UserDetailsImpl(
                        userId,
                        "123456",
                        List.of(new SimpleGrantedAuthority("admin")))
        ));
    }

}
