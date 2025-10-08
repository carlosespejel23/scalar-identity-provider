package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.models.Permission;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.payload.request.SetRolePermissionsRequest;
import com.scalar.identityProvider.services.GlobalRoleService;
import com.scalar.identityProvider.services.PermissionService;
import com.scalar.identityProvider.services.UserTenantRoleService;
import com.scalar.identityProvider.security.TenantContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.List;
import java.util.Optional;


/**
 * Controller for managing global roles and their permissions.
 * Only accessible by SUPER_ADMIN users.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {

    /*
	 * Dependencies
	 */
    private GlobalRoleService globalRoleService;

    private PermissionService permissionService;

    private UserTenantRoleService userTenantRoleService;


    /*
     * Constructors
     */
    public RoleController(){}

    @Autowired
    public RoleController(GlobalRoleService globalRoleService, PermissionService permissionService, UserTenantRoleService userTenantRoleService) {
        this.globalRoleService = globalRoleService;
        this.permissionService = permissionService;
        this.userTenantRoleService = userTenantRoleService;
    }


    /**
     * List all global roles with their permissions (SUPER_ADMIN only).
     * 
     * @return List of global roles with permissions.
     */
    @GetMapping("/global/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listGlobalRoles() {
        return ResponseEntity.ok(globalRoleService.findAllActiveRoles());
    }


    /**
     * List all available permissions (SUPER_ADMIN only).
     * 
     * @return List of permissions.
     */
    @GetMapping("/global/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listGlobalPermissions() {
        return ResponseEntity.ok(permissionService.findAll());
    }


    /**
     * Set permissions for a specific global role (SUPER_ADMIN only).
     * 
     * @param request Request body containing role name and permission codes.
     * @return Success or error message.
     */
    @PostMapping("/global/set-permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> setGlobalRolePermissions(@RequestBody SetRolePermissionsRequest request) {
        try {
            var roleEnum = com.scalar.identityProvider.models.EmployeeRole.valueOf(request.getRoleName());
            GlobalRole role = globalRoleService.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Global role not found: " + request.getRoleName()));

            Set<Permission> permissions = permissionService.upsertByCodes(request.getPermissionCodes() == null ? Set.of() : request.getPermissionCodes());

            // Persist role with permissions
            globalRoleService.setPermissions(role, permissions);

            return ResponseEntity.ok(new MessageResponse("success", "Permissions updated for role " + request.getRoleName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Invalid role name"));
        }
    }

    /**
     * List roles and permissions for the current tenant.
     * Accessible by ADMIN users within their tenant.
     * 
     * @return List of roles and permissions for the current tenant.
     */
    @GetMapping("/tenant/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listTenantRoles() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Tenant context not found"));
        }

        List<UserTenantRole> tenantRoles = userTenantRoleService.getTenantUsers(tenantId);
        return ResponseEntity.ok(tenantRoles);
    }

    /**
     * Get permissions for a specific user in the current tenant.
     * Accessible by ADMIN users within their tenant.
     * 
     * @param userId The user ID to get permissions for.
     * @return List of permissions for the user.
     */
    @GetMapping("/tenant/user/{userId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserPermissionsInTenant(@PathVariable String userId) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Tenant context not found"));
        }

        Optional<UserTenantRole> userTenantRole = userTenantRoleService.getUserRolesInTenant(userId, tenantId);
        if (userTenantRole.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "User not found in this tenant"));
        }

        var permissions = userTenantRole.get().getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return ResponseEntity.ok(permissions);
    }

    /**
     * Update user roles in the current tenant.
     * Accessible by ADMIN users within their tenant.
     * 
     * @param userId The user ID to update roles for.
     * @param roles The new roles to assign.
     * @return Success or error message.
     */
    @PutMapping("/tenant/user/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRolesInTenant(@PathVariable String userId, @RequestBody Set<String> roles) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Tenant context not found"));
        }

        try {
            userTenantRoleService.assignRolesToUser(userId, tenantId, roles);
            return ResponseEntity.ok(new MessageResponse("success", "User roles updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Failed to update user roles: " + e.getMessage()));
        }
    }
}


