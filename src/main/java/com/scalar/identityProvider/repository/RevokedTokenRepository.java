package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.RevokedToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing revoked tokens (blacklist)
 */
@Repository
public interface RevokedTokenRepository extends MongoRepository<RevokedToken, String> {
    
    /**
     * Check if a token is on the blacklist
     * 
     * @param token the token to check
     * @return true if the token is revoked, false otherwise
     */
    boolean existsByToken(String token);
    
    /**
     * Find a revoked token by its token
     * 
     * @param token the token to find
     * @return an Optional containing the RevokedToken if found, or empty if not found
     */
    Optional<RevokedToken> findByToken(String token);
    
    /**
     * Remove expired tokens from the blacklist
     * 
     * @param currentTime the current time to compare with token expiration
     * @return a list of expired revoked tokens
     */
    @Query("{ 'expiresAt': { $lt: ?0 } }")
    List<RevokedToken> findExpiredTokens(Instant currentTime);
    
    /**
     * Delete all revoked tokens for a specific user
     * 
     * @param username the username of the user
     * @param tenantId the tenant ID of the user
     */
    void deleteByUsernameAndTenantId(String username, String tenantId);
}

