package com.kaua.ecommerce.auth.infrastructure.oauth2.authorization.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorizationJpaEntityRepository extends JpaRepository<AuthorizationEntity, String> {

    Optional<AuthorizationEntity> findByState(String state);

    @Query("select a from AuthorizationEntity a join a.authorizationCode ac where ac.value = :authorizationCode")
    Optional<AuthorizationEntity> findByAuthorizationCodeValue(@Param("authorizationCode") String authorizationCode);

    @Query("select a from AuthorizationEntity a join a.accessToken at where at.value = :accessToken")
    Optional<AuthorizationEntity> findByAccessTokenValue(@Param("accessToken") String accessToken);

    @Query("select ae from AuthorizationEntity ae join ae.refreshToken rt where rt.value = :refreshValue")
    Optional<AuthorizationEntity> findByRefreshTokenValue(@Param("refreshValue") String refreshValue);

    @Query("select a from AuthorizationEntity a join a.oidcIdToken oi where oi.value = :idToken")
    Optional<AuthorizationEntity> findByOidcIdTokenValue(@Param("idToken") String idToken);

    @Query("select a from AuthorizationEntity a join a.userCode uc where uc.value = :userCode")
    Optional<AuthorizationEntity> findByUserCodeValue(@Param("userCode") String userCode);

    @Query("select a from AuthorizationEntity a join a.deviceCode dc where dc.value = :deviceCode")
    Optional<AuthorizationEntity> findByDeviceCodeValue(@Param("deviceCode") String deviceCode);

    @Query("select a from AuthorizationEntity a " +
            "left join a.authorizationCode ac " +
            "left join a.accessToken at " +
            "left join a.refreshToken rt " +
            "left join a.oidcIdToken oi " +
            "left join a.userCode uc " +
            "left join a.deviceCode dc " +
            "where a.state = :token " +
            "or ac.value = :token " +
            "or at.value = :token " +
            "or rt.value = :token " +
            "or oi.value = :token " +
            "or uc.value = :token " +
            "or dc.value = :token")
    Optional<AuthorizationEntity> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(@Param("token") String token);
}
