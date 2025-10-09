package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.RefreshToken;
import com.scalar.identityProvider.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Service for managing refresh tokens
 */
@Service
public class RefreshTokenService {
    
    /*
     * Dependecies
     */
    private RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refreshtokenexpiration.ms}") // 30 days by default
    private long refreshTokenExpirationMs;

    /**
     * Constructor
     * 
     * @param refreshTokenRepository The repository for refresh token operations
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    

    /**
     * Generate a new refresh token for a user
     * 
     * @param username The username
     * @param tenantId The tenant ID
     * @return The generated RefreshToken
     */
    @Transactional
    public RefreshToken generateRefreshToken(String username, String tenantId) {
        // Revoke existing user tokens
        revokeAllUserTokens(username, tenantId);
        
        // Create new refresh token
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs);
        
        RefreshToken refreshToken = new RefreshToken(token, username, tenantId, expiresAt);
        return refreshTokenRepository.save(refreshToken);
    }
    

    /**
     * Validate a refresh token
     * 
     * @param token The refresh token
     * @return true if valid, false otherwise
     */
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.isPresent() && 
               !refreshToken.get().isRevoked() && 
               !refreshToken.get().isExpired();
    }


    /**
     * Validates a refresh token for a specific tenant
     * 
     * @param token The refresh token
     * @param tenantId The tenant ID
     * @return true if valid for the tenant, false otherwise
     */
    public boolean validateRefreshTokenForTenant(String token, String tenantId) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.isPresent() && 
               !refreshToken.get().isRevoked() && 
               !refreshToken.get().isExpired() &&
               refreshToken.get().getTenantId().equals(tenantId);
    }
    

    /**
     * Obtains a refresh token for its token
     * 
     * @param token The refresh token
     * @return An Optional containing the RefreshToken if found, or empty if not found.
     */
    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    

    /**
     * Revoke a specific refresh token
     * 
     * @param token The refresh token to revoke
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshToken.get().setRevoked(true);
            refreshTokenRepository.save(refreshToken.get());
        }
    }
    

    /**
     * Revoke all refresh tokens for a user
     * 
     * @param username The username
     * @param tenantId The tenant ID
     */
    @Transactional
    public void revokeAllUserTokens(String username, String tenantId) {
        List<RefreshToken> userTokens = refreshTokenRepository.findByUsernameAndTenantId(username, tenantId);
        for (RefreshToken token : userTokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(userTokens);
    }
    

    /**
     * Clear expired tokens
     */
    public void cleanExpiredTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findExpiredTokens(Instant.now());
        refreshTokenRepository.deleteAll(expiredTokens);
    }
    

    /**
     * Get all active refresh tokens for a user
     * 
     * @param username The username
     * @param tenantId The tenant ID
     * @return A list of active RefreshTokens
     */
    public List<RefreshToken> getActiveUserTokens(String username, String tenantId) {
        return refreshTokenRepository.findActiveTokensByUserAndTenant(username, tenantId, Instant.now());
    }
}

