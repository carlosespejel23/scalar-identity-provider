package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for creating or updating a tenant
 * 
 * @param name        The name of the tenant
 * @param tenantId    The unique identifier for the tenant
 * @param description (optional) The description of the tenant
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


    /*
     * Default constructor
     */
    public TenantRequest() {
    }

    /**
     * Parameterized constructor
     * 
     * @param name        the name of the tenant
     * @param tenantId    the unique identifier for the tenant
     * @param description the description of the tenant
     */
    public TenantRequest(String name, String tenantId, String description) {
        this.name = name;
        this.tenantId = tenantId;
        this.description = description;
    }
}
