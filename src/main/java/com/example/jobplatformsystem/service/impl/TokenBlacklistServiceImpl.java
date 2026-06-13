package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.entity.BlacklistedToken;
import com.example.jobplatformsystem.repository.BlacklistedTokenRepository;
import com.example.jobplatformsystem.security.JwtService;
import com.example.jobplatformsystem.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl
        implements TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtService jwtService;

    @Override
    public void blacklistToken(String token) {

        if (blacklistedTokenRepository.existsByToken(token)) {
            return;
        }

        BlacklistedToken blacklistedToken =
                BlacklistedToken.builder()
                        .token(token)
                        .expiryDate(
                                jwtService.extractExpiration(token)
                                        .toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDateTime()
                        )
                        .build();

        blacklistedTokenRepository.save(blacklistedToken);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}