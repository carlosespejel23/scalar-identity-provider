package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.repository.TenantRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Service for managing tenant-related operations.
 */
@Service
public class TenantService {

    /*
     * Dependencies
     */
    private TenantRepository tenantRepository;

    /**
     * Constructor for TenantService.
     *
     * @param tenantRepository The repository for tenant operations.
     */
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }


    /**
     * Create a new tenant.
     *
     * @param tenant Tenant to create.
     * @return Tenant created.
     */
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }


    /**
     * Search for a tenant by ID.
     *
     * @param id Tenant ID.
     * @return An Optional containing the tenant if found.
     */
    public Optional<Tenant> findById(String id) {
        return tenantRepository.findById(id);
    }


    /**
     * Search for a tenant by its tenantId.
     *
     * @param tenantId The tenantId of the tenant.
     * @return An Optional containing the tenant if found.
     */
    public Optional<Tenant> findByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }


    /**
     * Get all active tenants.
     *
     * @return List of all active tenants.
     */
    public List<Tenant> findAllActiveTenants() {
        return tenantRepository.findAll().stream()
                .filter(Tenant::isActive)
                .toList();
    }


    /**
     * Check if a tenantId already exists.
     *
     * @param tenantId The tenantId to verify.
     * @return true if it exists, false otherwise.
     */
    public boolean existsByTenantId(String tenantId) {
        return tenantRepository.existsByTenantId(tenantId);
    }


    /**
     * Check if a tenant name already exists.
     *
     * @param name The name to be verified.
     * @return true if it exists, false otherwise.
     */
    public boolean existsByName(String name) {
        return tenantRepository.existsByName(name);
    }


    /**
     * Update a tenant.
     *
     * @param tenant The tenant to be updated.
     * @return The updated tenant.
     */
    @Transactional
    public Tenant updateTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }


    /**
     * Deactivate a tenant by its ID.
     * 
     * @param id The ID of the tenant to be deactivated.
     * @return The deactivated tenant.
     */
    @Transactional
    public Optional<Tenant> deactivateTenant(String id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isPresent()) {
            Tenant tenant = tenantOpt.get();
            tenant.setActive(false);
            tenant.setUpdatedAt(java.time.Instant.now().toString());
            tenantRepository.save(tenant);
            return Optional.of(tenant);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Reactivate a tenant by its ID.
     * 
     * @param id
     * @return The reactivated tenant.
     */
    @Transactional
    public Optional<Tenant> reactivateTenant(String id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (tenantOpt.isPresent()) {
            Tenant tenant = tenantOpt.get();
            tenant.setActive(true);
            tenant.setUpdatedAt(java.time.Instant.now().toString());
            tenantRepository.save(tenant);
            return Optional.of(tenant);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Delete a tenant by its ID.
     *
     * @param id The ID of the tenant to be deleted.
     */
    public void deleteTenant(String id) {
        tenantRepository.deleteById(id);
    }
}
