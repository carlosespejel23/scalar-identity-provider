package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload para refresh token
 * 
 * @param refreshToken The refresh token
 */
public class RefreshTokenRequest {
    
    /*
     * Refresh token
     */
    @NotBlank
    @Getter
    @Setter
    private String refreshToken;
    

    /*
     * Default construtor
     */
    public RefreshTokenRequest() {}
    
    /**
     * Parametrized construtor
     * 
     * @param refreshToken the refresh token
     */
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

