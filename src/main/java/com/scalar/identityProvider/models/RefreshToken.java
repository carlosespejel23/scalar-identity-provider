package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Document(collection = "refresh_tokens")
public class RefreshToken {
    
    /*
     * Unique identifier for the refresh token
     */
    @Id
    @Getter
    @Setter
    private String id;
    
    /*
     * The actual refresh token string
     */
    @Indexed(unique = true)
    @Getter
    @Setter
    private String token;
    
    /*
     * Username associated with the refresh token
     */
    @Getter
    @Setter
    private String username;
    
    /*
     * Tenant ID associated with the refresh token
     */
    @Getter
    @Setter
    private String tenantId;
    
    /*
    * Timestamp when the token was created
    */
    @Getter
    @Setter
    private Instant createdAt;
    
    /*
     * Timestamp when the token expires
     */
    @Getter
    @Setter
    private Instant expiresAt;
    
    /*
     * Flag indicating if the token has been revoked
     */
    @Getter
    @Setter
    private boolean revoked;
    

    /*
     * Default constructor
     */
    public RefreshToken() {}
    
    /**
     * Parameterized constructor
     * 
     * @param token     The refresh token string
     * @param username  The username associated with the token
     * @param tenantId  The tenant ID associated with the token
     * @param expiresAt The expiration timestamp of the token
     */
    public RefreshToken(String token, String username, String tenantId, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.tenantId = tenantId;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
        this.revoked = false;
    }
    

    /*
     * Check if the token is expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}

