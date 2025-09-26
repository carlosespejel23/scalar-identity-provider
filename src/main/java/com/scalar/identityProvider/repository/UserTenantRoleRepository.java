package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.UserTenantRole;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for database operations related to UserTenantRole.
 */
public interface UserTenantRoleRepository extends MongoRepository<UserTenantRole, String> {

    /**
     * Search for a user's role assignment in a specific tenant.
     *
     * @param userId User ID.
     * @param tenantId Tenant ID.
     * @return An Optional containing the assignment if found.
     */
    Optional<UserTenantRole> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Search for all role assignments for a user.
     *
     * @param userId User ID.
     * @return List of user role assignments.
     */
    List<UserTenantRole> findByUserId(String userId);

    /**
     * Search for all role assignments in a specific tenant.
     *
     * @param tenantId Tenant ID.
     * @return List of role assignments in the tenant.
     */
    List<UserTenantRole> findByTenantId(String tenantId);

    /**
     * Check if a user has roles assigned in a tenant.
     *
     * @param userId User ID.
     * @param tenantId Tenant ID.
     * @return true if roles are assigned, false otherwise.
     */
    boolean existsByUserIdAndTenantId(String userId, String tenantId);
}
