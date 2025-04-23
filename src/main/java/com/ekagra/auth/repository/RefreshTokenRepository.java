package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ekagra.auth.entity.RefreshToken;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}