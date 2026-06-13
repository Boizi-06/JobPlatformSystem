package com.example.jobplatformsystem.service;

public interface TokenBlacklistService {

    void blacklistToken(String token);

    boolean isBlacklisted(String token);
}