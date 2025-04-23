package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ekagra.auth.entity.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}