package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.models.User;
import com.scalar.identityProvider.repository.TenantRepository;
import com.scalar.identityProvider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones de usuarios relacionadas con tenants.
 */
@Service
public class UserTenantService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Busca un usuario por username en todos los tenants.
     *
     * @param username El username del usuario
     * @return Una lista de usuarios con ese username (puede haber uno por tenant)
     */
    public List<User> findUsersByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Busca un usuario por username y tenantId.
     *
     * @param username El username del usuario
     * @param tenantId El ID del tenant
     * @return Un Optional que contiene el usuario si se encuentra
     */
    public Optional<User> findUserByUsernameAndTenantId(String username, String tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }

    /**
     * Obtiene todos los tenants donde un usuario tiene cuenta.
     *
     * @param username El username del usuario
     * @return Lista de tenants donde el usuario tiene cuenta
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
     * Verifica si un usuario existe en un tenant específico.
     *
     * @param username El username del usuario
     * @param tenantId El ID del tenant
     * @return true si el usuario existe en ese tenant
     */
    public boolean userExistsInTenant(String username, String tenantId) {
        return userRepository.existsByUsernameAndTenantId(username, tenantId);
    }
}
