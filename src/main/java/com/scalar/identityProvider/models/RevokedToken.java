package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Document(collection = "revoked_tokens")
public class RevokedToken {
    
    /*
     * Unique identifier for the revoked token
     */
    @Id
    @Getter
    @Setter
    private String id;
    
    /*
     * The revoked JWT token
     */
    @Indexed(unique = true)
    @Getter
    @Setter
    private String token;
    
    /*
     * Username associated with the revoked token
     */
    @Getter
    @Setter
    private String username;
    
    /*
     * Tenant ID associated with the revoked token
     */
    @Getter
    @Setter
    private String tenantId;
    
    /*
     * Timestamp when the token was revoked
     */
    @Getter
    @Setter
    private Instant revokedAt;
    
    /*
     * Expiration time of the revoked token
     */
    @Getter
    @Setter
    private Instant expiresAt;
    

    /*
     * Default constructor
     */
    public RevokedToken() {}
    
    /**
     * Parameterized constructor
     * 
     * @param token      The revoked JWT token
     * @param username   Username associated with the revoked token
     * @param tenantId   Tenant ID associated with the revoked token
     * @param expiresAt  Expiration time of the revoked token
     */
    public RevokedToken(String token, String username, String tenantId, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.tenantId = tenantId;
        this.revokedAt = Instant.now();
        this.expiresAt = expiresAt;
    }
}

