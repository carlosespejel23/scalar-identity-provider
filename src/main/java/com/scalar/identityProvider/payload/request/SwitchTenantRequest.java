package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for switching tenant
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

    /*
     * Parameterized constructor
     */
    public SwitchTenantRequest(String tenantId) {
        this.tenantId = tenantId;
    }
}
