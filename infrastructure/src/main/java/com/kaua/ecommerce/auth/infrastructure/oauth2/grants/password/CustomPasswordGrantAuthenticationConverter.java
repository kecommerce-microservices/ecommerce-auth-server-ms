package com.kaua.ecommerce.auth.infrastructure.oauth2.grants.password;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

public class CustomPasswordGrantAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(final HttpServletRequest request) {
        // grant_type (required): Value MUST be set to "password".
        final var aGrantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.PASSWORD.getValue().equals(aGrantType)) {
            return null;
        }

//        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        final var aUsername = request.getParameter(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(aUsername) || request.getParameterValues(OAuth2ParameterNames.USERNAME).length != 1) {
            OAuth2EndpointUtils.throwError(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    OAuth2ParameterNames.USERNAME,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        final var aPassword = request.getParameter(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(aPassword) || request.getParameterValues(OAuth2ParameterNames.PASSWORD).length != 1) {
            OAuth2EndpointUtils.throwError(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    OAuth2ParameterNames.PASSWORD,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        final var aClientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (aClientPrincipal == null) {
            OAuth2EndpointUtils.throwError(
                    OAuth2ErrorCodes.INVALID_CLIENT,
                    OAuth2ParameterNames.CLIENT_ID,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

//        Map<String, Object> aAdditionalParameters = request.getParameterMap().entrySet().stream()
//                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE))
//                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        return new CustomPasswordGrantAuthenticationToken(
                AuthorizationGrantType.PASSWORD,
                aClientPrincipal,
                null
        );
    }
}
