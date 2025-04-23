package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ekagra.auth.entity.OauthToken;
import java.util.Optional;


public interface OauthTokenRepository extends JpaRepository<OauthToken, Long> {
    Optional<OauthToken> findByAccessToken(String accessToken);
    Optional<OauthToken> findByRefreshToken(String refreshToken);
}