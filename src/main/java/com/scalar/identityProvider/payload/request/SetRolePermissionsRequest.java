package com.scalar.identityProvider.payload.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Request body for setting role permissions.
 */
public class SetRolePermissionsRequest {
    
    /*
     * Name of the role.
     */
    @NotBlank
    @Getter
    @Setter
    private String roleName; // e.g., ROLE_ADMIN

    /*
     * Set of permission codes to assign to the role.
     */
    @Getter
    @Setter
    private Set<String> permissionCodes; // e.g., ["MANAGE_USERS", "COMPONENT:AdminPanel"]
}
