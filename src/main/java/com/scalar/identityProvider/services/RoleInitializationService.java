package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.Role;
import com.scalar.identityProvider.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio para inicializar roles por tenant.
 */
@Service
public class RoleInitializationService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Inicializa los roles básicos para un tenant específico.
     *
     * @param tenantId El ID del tenant.
     */
    public void initializeRolesForTenant(String tenantId) {
        List<EmployeeRole> rolesToCreate = Arrays.asList(
                EmployeeRole.ROLE_USER,
                EmployeeRole.ROLE_MODERATOR,
                EmployeeRole.ROLE_ADMIN
        );

        for (EmployeeRole roleName : rolesToCreate) {
            // Verificar si el rol ya existe para este tenant
            if (!roleRepository.findByNameAndTenantId(roleName, tenantId).isPresent()) {
                Role role = new Role(roleName, tenantId);
                String now = Instant.now().toString();
                role.setCreatedAt(now);
                role.setUpdatedAt(now);
                roleRepository.save(role);
            }
        }
    }
}
