package com.scalar.identityProvider.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;


/**
 * Service to automatically clean expired tokens
 */
@Service
public class TokenCleanupService {
    
    /*
     * Dependencies
     */
    private RefreshTokenService refreshTokenService;
    
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Constructor
     * 
     * @param refreshTokenService the refresh token service
     * @param tokenBlacklistService the token blacklist service
     */
    public TokenCleanupService(RefreshTokenService refreshTokenService, TokenBlacklistService tokenBlacklistService) {
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    

    /**
     * Clears expired tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanExpiredTokens() {
        try {
            // Clear expired refresh tokens
            refreshTokenService.cleanExpiredTokens();
            
            // Clear expired revoked tokens
            tokenBlacklistService.cleanExpiredTokens();
            
            IO.println("Cleaning of expired tokens completed: " + Instant.now());
        } catch (Exception e) {
            IO.println("Error during token cleanup: " + e.getMessage());
        }
    }
}

