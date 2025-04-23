package com.ekagra.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ekagra.auth.entity.Role;
import com.ekagra.auth.entity.User;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT t FROM User t JOIN FETCH t.role WHERE t.isApproved = false")
    List<User> findAllPendingApproval();

    @Modifying
    @Transactional
    @Query("UPDATE User t SET t.isActive = true WHERE t.email = :email")
    void markUserAsActive(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User t SET t.role.id = :roleId, t.isActive = :isActive, t.isApproved = :isApproved WHERE t.email = :email")
    int updateUserStatus(
        @Param("roleId") Long roleId,
        @Param("isActive") boolean isActive,
        @Param("isApproved") boolean isApproved,
        @Param("email") String email
    );

    @Modifying
    @Transactional
    @Query("UPDATE User t SET t.password = :password WHERE t.email = :email")
    void updatePassword(@Param("email") String email, @Param("password") String password);


}