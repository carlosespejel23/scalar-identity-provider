package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.repository.UserTenantRoleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Service for managing operations related to user roles per tenant.
 */
@Service
public class UserTenantRoleService {

    /*
     * Dependencies
     */
    private UserTenantRoleRepository userTenantRoleRepository;

    private GlobalRoleService globalRoleService;

    /**
     * Constructor for dependency injection.
     *
     * @param userTenantRoleRepository Repository for UserTenantRole entities.
     * @param globalRoleService Service for managing global roles.
     */
    public UserTenantRoleService(
        UserTenantRoleRepository userTenantRoleRepository,
        GlobalRoleService globalRoleService) {
        this.userTenantRoleRepository = userTenantRoleRepository;
        this.globalRoleService = globalRoleService;
    }


    /**
     * Assign roles to a user in a specific tenant.
     *
     * @param userId The user ID.
     * @param tenantId The tenant ID.
     * @param roleNames The names of the roles to be assigned.
     * @return The role assignment created or updated.
     */
    @Transactional
    public UserTenantRole assignRolesToUser(String userId, String tenantId, Set<String> roleNames) {
        Optional<UserTenantRole> existingAssignment = userTenantRoleRepository
                .findByUserIdAndTenantId(userId, tenantId);

        UserTenantRole userTenantRole;
        if (existingAssignment.isPresent()) {
            userTenantRole = existingAssignment.get();
        } else {
            userTenantRole = new UserTenantRole(userId, tenantId);
        }

        Set<GlobalRole> roles = new HashSet<>();
        for (String roleName : roleNames) {
            EmployeeRole employeeRole = mapStringToEmployeeRole(roleName);
            Optional<GlobalRole> globalRole = globalRoleService.findByName(employeeRole);
            globalRole.ifPresent(roles::add);
        }

        userTenantRole.setRoles(roles);
        return userTenantRoleRepository.save(userTenantRole);
    }


    /**
     * Get the roles of a user in a specific tenant.
     *
     * @param userId The user ID.
     * @param tenantId The tenant ID.
     * @return An Optional containing the role assignment, if it exists.
     */
    public Optional<UserTenantRole> getUserRolesInTenant(String userId, String tenantId) {
        return userTenantRoleRepository.findByUserIdAndTenantId(userId, tenantId);
    }


    /**
     * Get all tenants where a user has assigned roles.
     *
     * @param userId The user ID.
     * @return List of user role assignments.
     */
    public List<UserTenantRole> getUserTenants(String userId) {
        return userTenantRoleRepository.findByUserId(userId);
    }


    /**
     * Get all users with roles in a specific tenant.
     *
     * @param tenantId The tenant ID.
     * @return List of role assignments in the tenant.
     */
    public List<UserTenantRole> getTenantUsers(String tenantId) {
        return userTenantRoleRepository.findByTenantId(tenantId);
    }


    /**
     * Check if a user has a specific role in a tenant.
     *
     * @param userId The user ID.
     * @param tenantId The tenant ID.
     * @param roleName The role name.
     * @return true if the user has the role, false otherwise.
     */
    public boolean userHasRoleInTenant(String userId, String tenantId, EmployeeRole roleName) {
        Optional<UserTenantRole> userTenantRole = getUserRolesInTenant(userId, tenantId);
        if (userTenantRole.isPresent()) {
            return userTenantRole.get().getRoles().stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        }
        return false;
    }


    /**
     * Remove a user's role assignment in a tenant.
     *
     * @param userId The user ID.
     * @param tenantId The tenant ID.
     */
    public void removeUserFromTenant(String userId, String tenantId) {
        Optional<UserTenantRole> userTenantRole = userTenantRoleRepository
                .findByUserIdAndTenantId(userId, tenantId);
        userTenantRole.ifPresent(role -> userTenantRoleRepository.delete(role));
    }


    /**
     * Maps a string to an EmployeeRole.
     *
     * @param roleName The role name as String.
     * @return The corresponding EmployeeRole.
     */
    private EmployeeRole mapStringToEmployeeRole(String roleName) {
        switch (roleName.toLowerCase()) {
            case "user":
                return EmployeeRole.ROLE_USER;
            case "mod":
            case "moderator":
                return EmployeeRole.ROLE_MODERATOR;
            case "admin":
                return EmployeeRole.ROLE_ADMIN;
            case "super_admin":
            case "superadmin":
                return EmployeeRole.ROLE_SUPER_ADMIN;
            default:
                return EmployeeRole.ROLE_USER;
        }
    }
}
