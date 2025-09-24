package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.repository.GlobalRoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing operations related to global roles.
 */
@Service
public class GlobalRoleService {

    @Autowired
    private GlobalRoleRepository globalRoleRepository;

    /**
     * Initialize all global system roles.
     */
    public void initializeGlobalRoles() {
        List<EmployeeRole> rolesToCreate = Arrays.asList(
                EmployeeRole.ROLE_USER,
                EmployeeRole.ROLE_MODERATOR,
                EmployeeRole.ROLE_ADMIN,
                EmployeeRole.ROLE_SUPER_ADMIN
        );

        for (EmployeeRole roleName : rolesToCreate) {
            if (!globalRoleRepository.existsByName(roleName)) {
                String description = getRoleDescription(roleName);
                GlobalRole role = new GlobalRole(roleName, description);
                globalRoleRepository.save(role);
            }
        }
    }

    /**
     * Get a global role by name.
     *
     * @param roleName The name of the role.
     * @return An Optional containing the role if found.
     */
    public Optional<GlobalRole> findByName(EmployeeRole roleName) {
        return globalRoleRepository.findByName(roleName);
    }

    /**
     * Get all active global roles.
     *
     * @return List of all active global roles.
     */
    public List<GlobalRole> findAllActiveRoles() {
        return globalRoleRepository.findAll().stream()
                .filter(GlobalRole::isActive)
                .toList();
    }

    /**
     * Get the description of a role.
     *
     * @param roleName The name of the role.
     * @return The role description.
     */
    private String getRoleDescription(EmployeeRole roleName) {
        switch (roleName) {
            case ROLE_USER:
                return "Basic user with limited permissions";
            case ROLE_MODERATOR:
                return "Moderator with content management permissions";
            case ROLE_ADMIN:
                return "Administrator with full permissions in your tenant";
            case ROLE_SUPER_ADMIN:
                return "Super administrator with global system permissions";
            default:
                return "System role";
        }
    }
}
