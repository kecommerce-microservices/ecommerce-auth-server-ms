package com.kaua.ecommerce.auth.infrastructure.configurations;

import com.kaua.ecommerce.auth.application.gateways.MfaGateway;
import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.infrastructure.configurations.authentication.JwtConverter;
import com.kaua.ecommerce.auth.infrastructure.constants.Constants;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa.MfaAuthenticationFilter;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa.MfaAuthenticationProvider;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password.CustomPasswordGrantAuthenticationConverter;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password.CustomPasswordGrantAuthenticationProvider;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.utils.CustomTokenClaimsUtils;
import com.kaua.ecommerce.auth.infrastructure.services.KeysService;
import com.kaua.ecommerce.auth.infrastructure.userdetails.UserDetailsImpl;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
    public SecurityFilterChain defaultSecurityFilterChain(
            final HttpSecurity http,
            final AuthenticationManager authenticationManager,
            final UserRepository userRepository
    ) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtConverter())))
                .addFilterAfter(
                        new MfaAuthenticationFilter(
                                authenticationManager,
                                userRepository
                        ),
                        BasicAuthenticationFilter.class
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/assets/**", "/webjars/**", "/css/**", "/login", "/error").permitAll()
                        .requestMatchers("/v1/roles/**").hasAnyAuthority("manage-roles", "*")
                        .requestMatchers("/v1/users/add-roles", "/v1/users/remove-role").hasAnyAuthority("manage-users-roles", "*")
                        .requestMatchers("/v1/oauth2-clients/**").hasAnyAuthority("manage-oauth2-clients", "*")
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain

                .formLogin(formLogin ->
                        formLogin.loginPage("/login").permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Permite acesso apenas a este domínio
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Profile("!test-integration")
    @Bean
    public JWKSource<SecurityContext> jwkSource(final KeysService keysService) {
        final var aKeyPair = keysService.getKeyPairOrGenerate(
                Constants.JWT_PUBLIC_KEY,
                Constants.JWT_PRIVATE_KEY
        );

        final var aPublicKey = (RSAPublicKey) aKeyPair.getPublic();
        final var aPrivateKey = (RSAPrivateKey) aKeyPair.getPrivate();

        final var aRsaKey = new RSAKey.Builder(aPublicKey)
                .privateKey(aPrivateKey)
                .keyID(aPublicKey.getPublicExponent().toString())
                .build();

        JWKSet jwkSet = new JWKSet(aRsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(final JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            final var authGrantType = context.getAuthorizationGrantType();
            final var aClientCredentialsType = AuthorizationGrantType.CLIENT_CREDENTIALS.getValue();

            if (authGrantType.getValue().equals(aClientCredentialsType)) {
                context.getClaims()
                        .claim(CustomTokenClaimsUtils.AUTHORITIES, Set.of(CustomTokenClaimsUtils.ALL_AUTHORITIES))
                        .claim(CustomTokenClaimsUtils.IS_MICROSERVICE, true);
                return;
            }

            // Add custom claims to the JWT
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                final var aUserDetails = (UserDetailsImpl) context.getPrincipal().getPrincipal();

                context.getClaims()
                        .claim(CustomTokenClaimsUtils.AUTHORITIES, aUserDetails.getAuthorities()
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
    public AuthenticationManager authenticationManager(
            final UserRepository userRepository,
            final UserDetailsService userDetailsService,
            final MfaGateway mfaGateway
    ) {
        final var aMfaProvider = new MfaAuthenticationProvider(userRepository, mfaGateway);
        final var aDaoProvider = new DaoAuthenticationProvider();
        aDaoProvider.setUserDetailsService(userDetailsService);
        aDaoProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(aMfaProvider, aDaoProvider);
    }
}
