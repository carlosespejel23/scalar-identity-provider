package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.Permission;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for database operations related to Permission.
 */
public interface PermissionRepository extends MongoRepository<Permission, String> {
    /**
     * Search for a permission by code.
     * 
     * @param code The code of the permission.
     * @return An Optional containing the Permission if found.
     */
    Optional<Permission> findByCode(String code);

    /**
     * Check if a permission exists by its code.
     * 
     * @param code The code of the permission.
     * @return true if it exists, false otherwise.
     */
    boolean existsByCode(String code);
}


