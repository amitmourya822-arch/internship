package com.handholding.repository;

import com.handholding.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    AuthUser findByUsername(String username);
}