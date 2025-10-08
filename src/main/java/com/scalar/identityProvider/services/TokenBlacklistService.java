package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.RevokedToken;
import com.scalar.identityProvider.repository.RevokedTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


/**
 * Service for managing the blacklist of revoked tokens
 */
@Service
public class TokenBlacklistService {
    
    /*
     * Dependencies
     */
    private RevokedTokenRepository revokedTokenRepository;

    /**
     * Constructor
     * 
     * @param revokedTokenRepository Repository of revoked tokens
     */
    public TokenBlacklistService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }
    

    /**
     * Add a token to the blacklist
     * 
     * @param token The token to be blacklisted
     * @param username The username associated with the token
     * @param tenantId The tenant ID associated with the token
     * @param expiresAt The expiration time of the token
     */
    @Transactional
    public void addToBlacklist(String token, String username, String tenantId, Instant expiresAt) {
        RevokedToken revokedToken = new RevokedToken(token, username, tenantId, expiresAt);
        revokedTokenRepository.save(revokedToken);
    }
    

    /**
     * Check if a token is on the blacklist
     * 
     * @param token The token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
    

    /**
     * Revoke all tokens for a specific user
     * 
     * @param username The username whose tokens are to be revoked
     * @param tenantId The tenant ID associated with the user
     */
    public void revokeAllUserTokens(String username, String tenantId) {
        revokedTokenRepository.deleteByUsernameAndTenantId(username, tenantId);
    }
    

    /**
     * Clears expired tokens from the blacklist
     */
    public void cleanExpiredTokens() {
        List<RevokedToken> expiredTokens = revokedTokenRepository.findExpiredTokens(Instant.now());
        revokedTokenRepository.deleteAll(expiredTokens);
    }
}

