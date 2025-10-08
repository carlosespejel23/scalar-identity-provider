package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.repository.TenantRepository;
import com.scalar.identityProvider.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * Service for managing user operations related to tenants.
 */
@Service
public class UserTenantService {

    /*
     * Dependencies
     */
    private UserRepository userRepository;

    private TenantRepository tenantRepository;

    /**
     * Constructor
     * 
     * @param userRepository User repository
     * @param tenantRepository Tenant repository
     */
    public UserTenantService(
        UserRepository userRepository,
        TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }


    /**
     * Search for a user by username in all tenants.
     *
     * @param username The user's username
     * @return A list of users with that username (there may be one per tenant)
     */
    public List<User> findUsersByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    /**
     * Search for a user by username and tenantId.
     *
     * @param username The user's username
     * @param tenantId The tenant ID
     * @return An Optional containing the user if found
     */
    public Optional<User> findUserByUsernameAndTenantId(String username, String tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }


    /**
     * Get all tenants where a user has an account.
     *
     * @param username The user's username
     * @return List of tenants where the user has an account
     */
    public List<Tenant> getTenantsForUser(String username) {
        List<User> users = findUsersByUsername(username);
        return users.stream()
                .map(user -> tenantRepository.findByTenantId(user.getTenantId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }


    /**
     * Verify whether a user exists in a specific tenant.
     *
     * @param username The user's username
     * @param tenantId The tenant ID
     * @return true if the user exists in that tenant
     */
    public boolean userExistsInTenant(String username, String tenantId) {
        return userRepository.existsByUsernameAndTenantId(username, tenantId);
    }
}
