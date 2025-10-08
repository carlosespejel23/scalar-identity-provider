package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.payload.request.AddUserToTenantRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.repository.UserRepository;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.services.UserTenantRoleService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/*
 * Controller for managing users inside tenants.
 * Only accessible by admins.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/tenant-users")
public class UserTenantController {

    /*
     * Dependencies
     */
    private UserRepository userRepository;

    private TenantService tenantService;

    private UserTenantRoleService userTenantRoleService;


    /*
     * Constructors
     */
    public UserTenantController() {}

    @Autowired
    public UserTenantController(
        UserRepository userRepository,
        TenantService tenantService,
        UserTenantRoleService userTenantRoleService) {
        this.userRepository = userRepository;
        this.tenantService = tenantService;
        this.userTenantRoleService = userTenantRoleService;
    }


    /**
     * Add an existing user to a tenant with specific roles.
     *
     * @param addUserRequest The request to add a user to the tenant.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addUserToTenant(@Valid @RequestBody AddUserToTenantRequest addUserRequest) {
        
        // Verify that the tenant exists
        if (!tenantService.existsByTenantId(addUserRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Get user by email
        List<User> users = userRepository.findByEmail(addUserRequest.getEmail());
        if (users.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }
        User user = users.get(0);

        // Check if the user already has roles in this tenant
        if (userTenantRoleService.getUserRolesInTenant(user.getId(), addUserRequest.getTenantId()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "The user already has roles assigned in this tenant."));
        }

        // Assign default roles if not specified
        Set<String> roles = addUserRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("user");
        }

        // Assign roles to the user in the tenant
        userTenantRoleService.assignRolesToUser(user.getId(), addUserRequest.getTenantId(), roles);

        return ResponseEntity.ok(new MessageResponse("success", "User successfully added to tenant"));
    }


    /**
     * Get all users from a specific tenant.
     *
     * @param tenantId Tenant ID.
     * @return ResponseEntity with the list of users in the tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getTenantUsers(@PathVariable String tenantId) {
        
        // Verify that the tenant exists
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        List<UserTenantRole> tenantUsers = userTenantRoleService.getTenantUsers(tenantId);
        return ResponseEntity.ok(tenantUsers);
    }


    /**
     * Update user roles in a specific tenant.
     *
     * @param userId User ID.
     * @param tenantId Tenant ID.
     * @param roles New roles.
     * @return ResponseEntity with the result of the operation.
     */
    @PutMapping("/user/{userId}/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRolesInTenant(
            @PathVariable String userId,
            @PathVariable String tenantId,
            @RequestBody Set<String> roles) {
        
        // Verify that the user exists
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        // Verify that the tenant exists
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Assign default roles if not specified
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("user");
        }

        // Update user roles in the tenant
        userTenantRoleService.assignRolesToUser(userId, tenantId, roles);

        return ResponseEntity.ok(new MessageResponse("success", "User roles successfully updated"));
    }

    /**
     * Remove a user from a specific tenant.
     *
     * @param userId User ID.
     * @param tenantId Tenant ID.
     * @return ResponseEntity with the result of the operation.
     */
    @DeleteMapping("/user/{userId}/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> removeUserFromTenant(
            @PathVariable String userId,
            @PathVariable String tenantId) {
        
        // Verify that the user exists
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "User not found"));
        }

        // Verify that the tenant exists
        if (!tenantService.existsByTenantId(tenantId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Remove user from tenant
        userTenantRoleService.removeUserFromTenant(userId, tenantId);

        return ResponseEntity.ok(new MessageResponse("success", "User successfully removed from tenant"));
    }
}
