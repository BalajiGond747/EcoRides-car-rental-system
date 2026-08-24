package com.ecorides.repository;

import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByUserRole(UserRole userRole, Pageable pageable);

    Page<User> findByUserRoleAndIsActive(UserRole userRole, Boolean isActive, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.userRole = :userRole
            AND (
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<User> findByUserRoleAndSearch(@Param("userRole") UserRole userRole, @Param("search") String search, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.userRole = :userRole
            AND u.isActive = :isActive
            AND (
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<User> findByUserRoleAndIsActiveAndSearch(@Param("userRole") UserRole userRole, @Param("isActive") Boolean isActive, @Param("search") String search, Pageable pageable);

    long countByUserRoleAndIsActiveTrue(UserRole userRole);
}