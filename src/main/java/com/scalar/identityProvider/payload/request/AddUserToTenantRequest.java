package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/*
 * Request payload for adding a user to a tenant with specific roles
 */
public class AddUserToTenantRequest {

    /*
     * Username of the user
     */
    @NotBlank
    @Size(min = 3, max = 20)
    @Getter
    @Setter
    private String username;

    /*
     * Tenant ID to which the user is being added
     */
    @NotBlank
    @Size(max = 20)
    @Getter
    @Setter
    private String tenantId;

    /*
     * Roles assigned to the user in the tenant
     */
    @Getter
    @Setter
    private Set<String> roles;


    /*
     * Default constructor
     */
    public AddUserToTenantRequest() {
    }

    /*
     * Parameterized constructor
     */
    public AddUserToTenantRequest(String username, String tenantId, Set<String> roles) {
        this.username = username;
        this.tenantId = tenantId;
        this.roles = roles;
    }
}
