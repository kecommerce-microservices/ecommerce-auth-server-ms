package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "authorizations")
public class AuthorizationEntity {

    @Id
    private String id;

    @Column(name = "registered_client_id", nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", nullable = false)
    private String principalName;

    @Column(name = "authorization_grant_type", nullable = false)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", length = 1000)
    private String authorizedScopes;

    @Column(columnDefinition = "TEXT")
    private String attributes;

    @Column(length = 500)
    private String state;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AuthorizationCodeEntity authorizationCode;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AccessTokenEntity accessToken;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RefreshTokenEntity refreshToken;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OidcIdTokenEntity oidcIdToken;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserCodeEntity userCode;

    @OneToOne(mappedBy = "authorization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DeviceCodeEntity deviceCode;

    public AuthorizationEntity() {
    }

    public AuthorizationEntity(
            final String id,
            final String registeredClientId,
            final String principalName,
            final String authorizationGrantType,
            final String authorizedScopes,
            final String attributes,
            final String state,
            final AuthorizationCodeEntity authorizationCode,
            final AccessTokenEntity accessToken,
            final RefreshTokenEntity refreshToken,
            final OidcIdTokenEntity oidcIdToken,
            final UserCodeEntity userCode,
            final DeviceCodeEntity deviceCode
    ) {
        this.id = id;
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.authorizationGrantType = authorizationGrantType;
        this.authorizedScopes = authorizedScopes;
        this.attributes = attributes;
        this.state = state;
        this.authorizationCode = authorizationCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.oidcIdToken = oidcIdToken;
        this.userCode = userCode;
        this.deviceCode = deviceCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public String getAuthorizedScopes() {
        return authorizedScopes;
    }

    public void setAuthorizedScopes(String authorizedScopes) {
        this.authorizedScopes = authorizedScopes;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AuthorizationCodeEntity getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(AuthorizationCodeEntity authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public AccessTokenEntity getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessTokenEntity accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshTokenEntity getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshTokenEntity refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OidcIdTokenEntity getOidcIdToken() {
        return oidcIdToken;
    }

    public void setOidcIdToken(OidcIdTokenEntity oidcIdToken) {
        this.oidcIdToken = oidcIdToken;
    }

    public UserCodeEntity getUserCode() {
        return userCode;
    }

    public void setUserCode(UserCodeEntity userCode) {
        this.userCode = userCode;
    }

    public DeviceCodeEntity getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(DeviceCodeEntity deviceCode) {
        this.deviceCode = deviceCode;
    }
}
