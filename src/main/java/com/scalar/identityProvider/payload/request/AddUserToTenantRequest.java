package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for adding a user to a tenant with specific roles
 * 
 * @param email    The email of the user to be added
 * @param tenantId The ID of the tenant to which the user is being added
 * @param roles (optional) The roles assigned to the user in the tenant
 */
public class AddUserToTenantRequest {

    /*
     * Email of the user
     */
    @NotBlank
    @Size(min = 3, max = 20)
    @Email
    @Getter
    @Setter
    private String email;

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

    /**
     * Parameterized constructor
     * 
     * @param email    the email of the user
     * @param tenantId the ID of the tenant
     * @param roles    the roles assigned to the user
     */
    public AddUserToTenantRequest(String email, String tenantId, Set<String> roles) {
        this.email = email;
        this.tenantId = tenantId;
        this.roles = roles;
    }
}
