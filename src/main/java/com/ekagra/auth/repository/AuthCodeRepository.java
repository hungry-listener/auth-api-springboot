package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ekagra.auth.entity.AuthCode;
import java.util.Optional;


public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {
    Optional<AuthCode> findByCode(String code);
}