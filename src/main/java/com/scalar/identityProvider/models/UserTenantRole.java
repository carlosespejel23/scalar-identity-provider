package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "user_tenant_roles")
public class UserTenantRole {

    /*
     * Unique identifier for the UserTenantRole
     */
    @Id
    @Getter
    @Setter
    private String id;

    /*
     * User ID
     */
    @Getter
    @Setter
    private String userId;

    /*
     * Tenant ID
     */
    @Getter
    @Setter
    private String tenantId;

    /*
     * Roles assigned to the user within the tenant
     */
    @DBRef
    @Getter
    @Setter
    private Set<GlobalRole> roles = new HashSet<>();


    /*
     * Default constructor
     */
    public UserTenantRole() {
    }

    /**
     * Constructor with userId and tenantId
     * 
     * @param userId   The user ID
     * @param tenantId The tenant ID
     */
    public UserTenantRole(String userId, String tenantId) {
        this.userId = userId;
        this.tenantId = tenantId;
    }
}
