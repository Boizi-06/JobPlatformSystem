package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.entity.RefreshToken;
import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.exception.ResourceNotFoundException;
import com.example.jobplatformsystem.repository.RefreshTokenRepository;
import com.example.jobplatformsystem.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(
            User user) {

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                refreshTokenExpiration / 1000
                                        )
                        )
                        .user(user)
                        .build();

        return refreshTokenRepository.save(
                refreshToken
        );
    }

    @Override
    public RefreshToken verifyRefreshToken(
            String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Refresh token not found"));

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(
                    refreshToken
            );

            throw new RuntimeException(
                    "Refresh token expired");
        }

        return refreshToken;
    }
    @Override
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Refresh token not found"));

        refreshTokenRepository.delete(
                refreshToken
        );
    }
}