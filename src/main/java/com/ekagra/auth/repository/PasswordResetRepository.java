package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ekagra.auth.entity.PasswordReset;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByResetToken(String resetToken);

    boolean existsByResetTokenAndUsedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    @Query("SELECT COUNT(p) > 0 FROM PasswordReset p WHERE p.resetToken = :token AND p.used = false AND p.expiresAt > :now")
    boolean tokenIsValid(@Param("token") String token, @Param("now") LocalDateTime now);



    @Modifying
    @Transactional
    @Query("UPDATE PasswordReset t SET t.used = true WHERE t.resetToken = :resetToken")
    void markTokenAsUsed(@Param("resetToken") String resetToken);

    @Query("SELECT t.user.email FROM PasswordReset t WHERE t.resetToken = :resetToken")
    String findEmailByPasswordResetToken(@Param("resetToken") String resetToken);

}