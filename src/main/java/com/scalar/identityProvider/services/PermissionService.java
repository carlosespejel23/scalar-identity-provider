package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.Permission;
import com.scalar.identityProvider.repository.PermissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Service for managing operations related to permissions.
 */
@Service
public class PermissionService {

    /*
     * Dependencies
     */
    private PermissionRepository permissionRepository;

    /**
     * Constructor
     * 
     * @param permissionRepository The repository for permission operations.
     */
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    /**
     * Initialize default permissions.
     */
    @Transactional
    public void initializeDefaultPermissions() {
        // Define default permissions
        List<String> defaults = Arrays.asList(
                "VIEW_DASHBOARD",
                "MANAGE_USERS",
                "VIEW_REPORTS",
                "COMPONENT:UsersTable",
                "COMPONENT:AdminPanel"
        );
        // Ensure each default permission exists in the repository
        for (String code : defaults) {
            if (!permissionRepository.existsByCode(code)) {
                permissionRepository.save(new Permission(code, "Auto-created permission: " + code));
            }
        }
    }


    /**
     * Find a permission by its code.
     * 
     * @param code The permission code.
     * @return An Optional containing the Permission if found, or empty if not found.
     */
    public Optional<Permission> findByCode(String code) {
        return permissionRepository.findByCode(code);
    }


    /**
     * Get all permissions.
     * 
     * @return A list of all permissions.
     */
    public List<Permission> findAll() { 
        return permissionRepository.findAll(); 
    }


    /**
     * Save a permission.
     * 
     * @param permission The permission to save.
     * @return The saved permission.
     */
    @Transactional
    public Permission save(Permission permission) { 
        return permissionRepository.save(permission); 
    }


    /**
     * Upsert permissions by their codes. If a permission with the given code does not exist, it will be created.
     * 
     * @param codes A set of permission codes to upsert.
     * @return A set of upserted permissions.
     */
    public Set<Permission> upsertByCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) return Set.of();
        return codes.stream().map(code -> {
            return findByCode(code).orElseGet(() -> save(new Permission(code, "Auto-created permission: " + code)));
        }).collect(Collectors.toSet());
    }
}


