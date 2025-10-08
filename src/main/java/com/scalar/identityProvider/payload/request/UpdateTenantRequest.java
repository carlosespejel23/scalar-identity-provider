package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for updating tenant information (all fields are optional).
 * 
 * @param name        The name of the tenant (max 50 characters).
 * @param description The description of the tenant (max 200 characters).
 * @param logoUrl     The logo URL of the tenant (max 200 characters).
 * @param updatedAt   The date when the tenant was updated.
 */
public class UpdateTenantRequest {
    
    /*
     * Name of the tenant
     */
    @Size(max = 50)
    @Setter
    @Getter
    private String name;

    /*
     * Tenant ID (unique identifier)
     */
    @Size(max = 50)
    @Setter
    @Getter
    private String tenantId;

    /*
     * Description of the tenant
     */
    @Size(max = 200)
    @Setter
    @Getter
    private String description;

    /*
     * Logo URL of the tenant
     */
    @Size(max = 200)
    @Getter
    @Setter
    private String logoUrl;

    /*
     * Date when the tenant was updated
     */
    @Getter
    @Setter
    private String updatedAt;
}
