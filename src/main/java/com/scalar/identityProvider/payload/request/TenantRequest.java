package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for creating or updating a tenant
 */
public class TenantRequest {

    /*
     * Name of the tenant
     */
    @NotBlank
    @Size(max = 50)
    @Getter
    @Setter
    private String name;

    /*
     * Unique identifier for the tenant
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String tenantId;

    /*
     * Description of the tenant
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String description;

    public TenantRequest() {
    }

    public TenantRequest(String name, String tenantId, String description) {
        this.name = name;
        this.tenantId = tenantId;
        this.description = description;
    }
}
