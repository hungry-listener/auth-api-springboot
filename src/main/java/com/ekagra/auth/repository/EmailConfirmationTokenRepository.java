package com.ekagra.auth.repository;

import com.ekagra.auth.entity.EmailConfirmationToken;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Query("SELECT t.email FROM EmailConfirmationToken t WHERE t.token = :token")
    String findEmailByToken(@Param("token") String token);


    void deleteByToken(String token);

    boolean existsByTokenAndUsedFalseAndExpiryDateAfter(String token, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE EmailConfirmationToken t SET t.used = true WHERE t.token = :token")
    void markTokenAsUsed(@Param("token") String token);
}
