package com.ecorides.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecorides.entity.User;
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}