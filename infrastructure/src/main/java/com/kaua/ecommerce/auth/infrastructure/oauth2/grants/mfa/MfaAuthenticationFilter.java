package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.mfa;

import com.kaua.ecommerce.auth.application.repositories.UserRepository;
import com.kaua.ecommerce.auth.domain.users.User;
import com.kaua.ecommerce.auth.infrastructure.oauth2.grants.utils.CustomTokenClaimsUtils;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class MfaAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MfaAuthenticationFilter.class);

    private static final String MFA_URI_CREATE = "/mfa";
    private static final String MFA_DISABLE_URI = "/mfa/disable";
    private static final String MFA_CONFIRM_URI = "/mfa/device/confirm";
    private static final String MFA_VERIFY_URI = "/mfa/verify";

    private static final String MFA_ERROR_OTP = "MFA-ERROR-OTP";
    private static final String NO_OTP_CODE = "NO-OTP-CODE";
    private static final String DEVICE_NOT_VERIFIED = "DEVICE-NOT-VERIFIED";
    private static final String MFA_NOT_VERIFIED = "MFA-NOT-VERIFIED";

    private final AuthenticationManager manager;
    private final UserRepository userRepository;

    public MfaAuthenticationFilter(
            final AuthenticationManager manager,
            final UserRepository userRepository
    ) {
        this.manager = Objects.requireNonNull(manager);
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        final var aInitial = SecurityContextHolder.getContext().getAuthentication();

        if (aInitial == null) {
            log.debug("MFA Authentication Filter - Not authenticated");
            filterChain.doFilter(request, response);
            return;
        }

        if (aInitial.isAuthenticated()) {
            var aToken = (Jwt) aInitial.getCredentials();

            if (aToken == null) {
                log.debug("MFA Authentication Filter - No token found");
                filterChain.doFilter(request, response);
                return;
            }

            final var aIsMicroservice = aToken.getClaimAsBoolean(CustomTokenClaimsUtils.IS_MICROSERVICE);

            if (Boolean.TRUE.equals(aIsMicroservice)) {
                log.debug("MFA Authentication Filter - authenticated as microservice");
                filterChain.doFilter(request, response);
                return;
            }

            final var aUser = this.userRepository
                    .findById(UUID.fromString(aInitial.getName()))
                    .orElseThrow(NotFoundException.with(User.class, aInitial.getName()));

            final var aUserMfa = aUser.getMfa();

            if (!aUserMfa.isMfaEnabled()) {
                log.debug("MFA Authentication Filter - MFA not enabled");
                filterChain.doFilter(request, response);
                return;
            }

            if (aUserMfa.isValid()) {
                log.debug("MFA Authentication Filter - MFA already verified");
                filterChain.doFilter(request, response);
                return;
            }

            if (aUserMfa.isMfaEnabled() && !aUserMfa.isDeviceVerified() && request.getRequestURI().endsWith(MFA_URI_CREATE)) {
                log.debug("MFA Authentication Filter - MFA device not verified, but permits user to access and generate OTP");
                filterChain.doFilter(request, response);
                return;
            }

            if (aUserMfa.isMfaEnabled() && !aUserMfa.isDeviceVerified() && request.getRequestURI().contains(MFA_DISABLE_URI)) {
                log.debug("MFA Authentication Filter - MFA device not verified, but permits user to access and disable MFA");
                filterChain.doFilter(request, response);
                return;
            }

            if (!aUserMfa.isDeviceVerified() && !request.getRequestURI().contains(MFA_CONFIRM_URI)) {
                log.debug("MFA Authentication Filter - MFA device not verified, redirecting to MFA device confirmation");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "OTP");
                response.setHeader(MFA_ERROR_OTP, DEVICE_NOT_VERIFIED);
                return;
            }

            if (aUserMfa.isDeviceVerified() && !request.getRequestURI().contains(MFA_VERIFY_URI)) {
                log.debug("MFA Authentication Filter - MFA not verified, redirecting to MFA verification");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "OTP");
                response.setHeader(MFA_ERROR_OTP, MFA_NOT_VERIFIED);
                return;
            }

            final var aOtp = new MfaAuthenticationConverter(aInitial).convert(request);

            if (aOtp == null) {
                log.debug("MFA Authentication Filter - No OTP code provided");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "OTP");
                response.setHeader(MFA_ERROR_OTP, NO_OTP_CODE);
                return;
            }

            try {
                final var aAuth = this.manager.authenticate(aOtp);
                SecurityContextHolder.getContext().setAuthentication(aAuth);
                log.debug("MFA Authentication Filter - MFA verified");
            } catch (Exception e) {
                log.debug("MFA Authentication Filter - Error verifying MFA", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.debug("MFA Authentication Filter - Not authenticated");
        }
        filterChain.doFilter(request, response);
    }
}
