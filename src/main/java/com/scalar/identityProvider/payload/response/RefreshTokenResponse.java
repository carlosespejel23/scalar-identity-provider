package com.scalar.identityProvider.payload.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response payload para refresh token
 */
public class RefreshTokenResponse {
    
    /*
     * Status of the response
     */
    @Getter
    @Setter
    private String status;
    
    /*
     * Message content
     */
    @Getter
    @Setter
    private String message;
    
    /*
     * Access token
     */
    @Getter
    @Setter
    private String accessToken;
    
    /*
     * Refresh token
     */
    @Getter
    @Setter
    private String refreshToken;
    
    /*
     * Token type
     */
    @Getter
    @Setter
    private String tokenType = "Bearer";
    

    /**
     * Constructor to initialize the response
     * 
     * @param status       Status of the response
     * @param accessToken  Access token
     * @param refreshToken Refresh token
     * @param message      Message content
     */
    public RefreshTokenResponse(String status, String accessToken, String refreshToken, String message) {
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }
}

