package com.scalar.identityProvider.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "user_tenant_roles")
public class UserTenantRole {
    @Id
    private String id;

    private String userId;

    private String tenantId;

    @DBRef
    private Set<GlobalRole> roles = new HashSet<>();

    public UserTenantRole() {
    }

    public UserTenantRole(String userId, String tenantId) {
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Set<GlobalRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<GlobalRole> roles) {
        this.roles = roles;
    }
}
