package com.example.jobplatformsystem.service;

import com.example.jobplatformsystem.entity.RefreshToken;
import com.example.jobplatformsystem.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);
    void revokeRefreshToken(String token);
}