package com.scalar.identityProvider.controllers;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.payload.request.TenantRequest;
import com.scalar.identityProvider.payload.request.UpdateTenantRequest;
import com.scalar.identityProvider.payload.response.MessageResponse;
import com.scalar.identityProvider.services.TenantService;
import com.scalar.identityProvider.utils.TenantUtils;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


/*
 * Controller for managing tenants.
 * These endpoints are accessible by ADMIN and SUPER_ADMIN users.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController 
@RequestMapping("/api/admin/tenants")
public class TenantController {

    /*
     * Dependencies
     */
    private TenantService tenantService;


    /*
     * Constructors
     */
    public TenantController(){}

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    
    /**
     * Create a new tenant.
     *
     * @apiNote Only accessible by ADMIN and SUPER_ADMIN users.
     * @param tenantRequest The request to create a tenant.
     * @return ResponseEntity with the result of the operation.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createTenant(@Valid @RequestBody TenantRequest tenantRequest) {
        // Check if the name and tenantId already exist
        if (tenantService.existsByTenantId(tenantRequest.getTenantId()) || tenantService.existsByName(tenantRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "The tenant already exists. Try another name."));
        }

        // Get current timestamp
		String now = Instant.now().toString();

        // Create the tenant
        Tenant tenant = new Tenant(
                tenantRequest.getName(),
                tenantRequest.getTenantId(),
                now, // createdAt
                now // updatedAt
        );
        tenantService.createTenant(tenant);

        return ResponseEntity.ok(new MessageResponse("success", "Tenant created successfully"));
    }


    /**
     * Get all active tenants.
     *
     * @apiNote Only accessible by SUPER_ADMIN users.
     * @return ResponseEntity with the list of tenants.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.findAllActiveTenants();
        return ResponseEntity.ok(tenants);
    }


    /**
     * Obtain a tenant by its ID.
     *
     * @apiNote Only accessible by ADMIN and SUPER_ADMIN users.
     * @param id Tenant ID.
     * @return ResponseEntity with the tenant or error message.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTenantById(@PathVariable String id) {
        Optional<Tenant> tenant = tenantService.findById(id);
        if (tenant.isPresent()) {
            return ResponseEntity.ok(tenant.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }
    }

    
    /**
     * Obtain a tenant by its tenantId.
     *
     * @apiNote Only accessible by ADMIN and SUPER_ADMIN users.
     * @param tenantId The tenantId of the tenant.
     * @return ResponseEntity with the tenant or error message.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/by-tenant-id/{tenantId}")
    public ResponseEntity<?> getTenantByTenantId(@PathVariable String tenantId) {
        Optional<Tenant> tenant = tenantService.findByTenantId(tenantId);
        if (tenant.isPresent()) {
            return ResponseEntity.ok(tenant.get());
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }
    }


    /**
     * Update a tenant
     * 
     * @apiNote Only accessible by ADMIN and SUPER_ADMIN users.
     * @param id The ID of the tenant to update.
     * @param tenantRequest The request containing updated tenant details.
     * @return ResponseEntity with the result of the operation.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTenant(@PathVariable String id, @Valid @RequestBody UpdateTenantRequest tenantRequest) {
        // Check if the tenant exists
        Optional<Tenant> existingTenantOpt = tenantService.findById(id);
        if (existingTenantOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }
        Tenant existingTenant = existingTenantOpt.get();

        // Check if the new name or tenantId already exist for other tenants
        if (!existingTenant.getName().equals(tenantRequest.getName()) && tenantService.existsByName(tenantRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "The tenant name is already in use. Try another name."));
        }
        if (!existingTenant.getTenantId().equals(tenantRequest.getTenantId()) && tenantService.existsByTenantId(tenantRequest.getTenantId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "The tenant ID is already in use. Try another tenant ID."));
        }

        // If name is changed, change the tenantId to match the new name
        if (!existingTenant.getName().equals(tenantRequest.getName())) {
            String newTenantId = TenantUtils.generateTenantId(tenantRequest.getName());
            if (tenantService.existsByTenantId(newTenantId)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("error", "The derived tenant ID from the new name is already in use. Try another name."));
            }
            tenantRequest.setTenantId(newTenantId);
        }

        // Update the tenant details
        existingTenant.setName(tenantRequest.getName());
        existingTenant.setTenantId(tenantRequest.getTenantId());
        existingTenant.setDescription(tenantRequest.getDescription());
        existingTenant.setLogoUrl(tenantRequest.getLogoUrl());
        existingTenant.setUpdatedAt(Instant.now().toString());

        tenantService.updateTenant(existingTenant);

        return ResponseEntity.ok(new MessageResponse("success", "Tenant updated successfully"));
    }


    /**
     * Deactivate (soft delete) a tenant by its ID.
     *
     * @apiNote Only accessible by SUPER_ADMIN users.
     * @param id The ID of the tenant to deactivate.
     * @return ResponseEntity with the result of the operation.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateTenant(@PathVariable String id) {
        // Check if the tenant exists
        Optional<Tenant> existingTenantOpt = tenantService.findById(id);
        if (existingTenantOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Deactivate the tenant
        tenantService.deactivateTenant(id);

        return ResponseEntity.ok(new MessageResponse("success", "Tenant deactivated successfully"));
    }


    /**
     * Reactivate a tenant by its ID.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/reactivate/{id}")
    public ResponseEntity<?> reactivateTenant(@PathVariable String id) {
        // Check if the tenant exists
        Optional<Tenant> existingTenantOpt = tenantService.findById(id);
        if (existingTenantOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Reactivate the tenant
        tenantService.reactivateTenant(id);

        return ResponseEntity.ok(new MessageResponse("success", "Tenant reactivated successfully"));
    }


    /**
     * Delete a tenant permanently by its ID.
     * 
     * @apiNote Only accessible by SUPER_ADMIN users.
     * @param id The ID of the tenant to delete.
     * @return ResponseEntity with the result of the operation.
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTenant(@PathVariable String id) {
        // Check if the tenant exists
        Optional<Tenant> existingTenantOpt = tenantService.findById(id);
        if (existingTenantOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("error", "Tenant not found"));
        }

        // Delete the tenant permanently
        tenantService.deleteTenant(id);

        return ResponseEntity.ok(new MessageResponse("success", "Tenant deleted successfully"));
    }
}
