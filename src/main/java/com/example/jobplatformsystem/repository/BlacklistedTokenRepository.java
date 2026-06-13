package com.example.jobplatformsystem.repository;

import com.example.jobplatformsystem.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository
        extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
}