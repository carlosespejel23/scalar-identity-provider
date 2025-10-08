package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.models.Permission;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.payload.request.SetRolePermissionsRequest;
import com.scalar.identityProvider.services.GlobalRoleService;
import com.scalar.identityProvider.services.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


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


    /*
     * Constructors
     */
    public RoleController(){}

    @Autowired
    public RoleController(GlobalRoleService globalRoleService, PermissionService permissionService) {
        this.globalRoleService = globalRoleService;
        this.permissionService = permissionService;
    }


    /**
     * List all global roles with their permissions.
     * Only accessible by SUPER_ADMIN users.
     * 
     * @return List of global roles with permissions.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listRoles() {
        return ResponseEntity.ok(globalRoleService.findAllActiveRoles());
    }


    /**
     * List all available permissions.
     * Only accessible by SUPER_ADMIN users.
     * 
     * @return List of permissions.
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listPermissions() {
        return ResponseEntity.ok(permissionService.findAll());
    }


    /**
     * Set permissions for a specific global role.
     * Only accessible by SUPER_ADMIN users.
     * 
     * @param request Request body containing role name and permission codes.
     * @return Success or error message.
     */
    @PostMapping("/set-permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> setRolePermissions(@RequestBody SetRolePermissionsRequest request) {
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
}


