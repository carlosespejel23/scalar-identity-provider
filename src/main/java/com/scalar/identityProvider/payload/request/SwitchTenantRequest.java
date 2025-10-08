package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for switching tenant
 * 
 * @param tenantId The ID of the tenant to switch to
 */
public class SwitchTenantRequest {

    /*
     * The ID of the tenant to switch to
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String tenantId;


    /*
     * Default constructor
     */
    public SwitchTenantRequest() {
    }

    /**
     * Parameterized constructor
     * 
     * @param tenantId The ID of the tenant to switch to
     */
    public SwitchTenantRequest(String tenantId) {
        this.tenantId = tenantId;
    }
}
