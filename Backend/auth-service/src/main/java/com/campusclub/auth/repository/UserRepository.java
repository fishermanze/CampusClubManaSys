package com.campusclub.auth.repository;

import com.campusclub.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    Boolean existsByUsername(String username);

    Boolean existsByPhone(String phone);

    Boolean existsByEmail(String email);
}