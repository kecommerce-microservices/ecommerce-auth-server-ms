package com.kaua.ecommerce.auth.infrastructure.configurations;

import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password.CustomPasswordGrantAuthenticationConverter;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password.CustomPasswordGrantAuthenticationProvider;
import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            final HttpSecurity http,
            final AuthenticationManager authenticationManager,
            final OAuth2AuthorizationService authorizationService,
            final OAuth2TokenGenerator<?> tokenGenerator
    ) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        final var aAuthServer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());    // Enable OpenID Connect 1.0

        aAuthServer.tokenEndpoint(tokenEndpoint ->
                tokenEndpoint.accessTokenRequestConverter(
                                new CustomPasswordGrantAuthenticationConverter())
                        .authenticationProvider(
                                new CustomPasswordGrantAuthenticationProvider(
                                        authenticationManager,
                                        authorizationService,
                                        tokenGenerator
                                ))
        );

        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtConverter())))
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain

                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            final var authGrantType = context.getAuthorizationGrantType();
            final var aClientCredentialsType = AuthorizationGrantType.CLIENT_CREDENTIALS.getValue();

            if (authGrantType.getValue().equals(aClientCredentialsType)) {
                return;
            }

            // Add custom claims to the JWT
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                final var aUserDetails = (UserDetailsImpl) context.getPrincipal().getPrincipal();

                context.getClaims()
                        .claim("authorities", aUserDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet()));
            }
        };
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator(final JWKSource<SecurityContext> jwkSource) {
        final var aJwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
        aJwtGenerator.setJwtCustomizer(tokenCustomizer());

        final var aAccessTokenGenerator = new OAuth2AccessTokenGenerator();
        final var aRefreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                aJwtGenerator,
                aAccessTokenGenerator,
                aRefreshTokenGenerator
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        final var dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }
}
