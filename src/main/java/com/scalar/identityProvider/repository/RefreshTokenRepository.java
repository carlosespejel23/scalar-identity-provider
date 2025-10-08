package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.RefreshToken;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing refresh tokens
 */
@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    
    /**
     * Find a refresh token by its token
     * 
     * @param token The refresh token string
     * @return An Optional containing the RefreshToken if found
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find all active refresh tokens for a user
     * 
     * @param username The username of the user
     * @param tenantId The tenant ID
     * @param currentTime The current time to check for expiration
     * @return A list of active RefreshTokens
     */
    @Query("{ 'username': ?0, 'tenantId': ?1, 'revoked': false, 'expiresAt': { $gt: ?2 } }")
    List<RefreshToken> findActiveTokensByUserAndTenant(String username, String tenantId, Instant currentTime);
    
    /**
     * Find all refresh tokens for a user (active and inactive)
     * 
     * @param username The username of the user
     * @param tenantId The tenant ID
     * @return A list of all RefreshTokens
     */
    List<RefreshToken> findByUsernameAndTenantId(String username, String tenantId);
    
    /**
     * Find expired tokens
     * 
     * @param currentTime The current time to check for expiration
     * @return A list of expired RefreshTokens
     */
    @Query("{ 'expiresAt': { $lt: ?0 } }")
    List<RefreshToken> findExpiredTokens(Instant currentTime);
    
    /**
     * Revoke all refresh tokens for a specific user
     * 
     * @param username The username of the user
     * @param tenantId The tenant ID
     */
    @Query("{ 'username': ?0, 'tenantId': ?1 }")
    void revokeAllTokensByUserAndTenant(String username, String tenantId);
}

