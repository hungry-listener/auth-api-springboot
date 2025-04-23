
package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ekagra.auth.entity.OauthClient;
import java.util.Optional;

public interface OauthClientRepository extends JpaRepository<OauthClient, Long> {
    Optional<OauthClient> findByClientId(String clientId);
}