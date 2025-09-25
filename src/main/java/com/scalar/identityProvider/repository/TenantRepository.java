package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.Tenant;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for database operations related to Tenant.
 */
public interface TenantRepository extends MongoRepository<Tenant, String> {

    /**
     * Search for a tenant by its tenantId.
     *
     * @param tenantId Tenant ID.
     * @return An Optional containing the Tenant if found, or empty if not found.
     */
    Optional<Tenant> findByTenantId(String tenantId);

    /**
     * Check if a tenantId already exists in the database.
     *
     * @param tenantId The tenantId to verify.
     * @return A Boolean indicating whether the tenantId exists (true) or not (false).
     */
    Boolean existsByTenantId(String tenantId);

    /**
     * Check if a tenant name already exists in the database.
     *
     * @param name The name of the tenant to be verified.
     * @return A Boolean indicating whether the name exists (true) or not (false).
     */
    Boolean existsByName(String name);
}
