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
 * Servicio para manejar operaciones relacionadas con roles globales.
 */
@Service
public class GlobalRoleService {

    @Autowired
    private GlobalRoleRepository globalRoleRepository;

    /**
     * Inicializa todos los roles globales del sistema.
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
     * Obtiene un rol global por su nombre.
     *
     * @param roleName El nombre del rol.
     * @return Un Optional que contiene el rol si se encuentra.
     */
    public Optional<GlobalRole> findByName(EmployeeRole roleName) {
        return globalRoleRepository.findByName(roleName);
    }

    /**
     * Obtiene todos los roles globales activos.
     *
     * @return Lista de todos los roles globales activos.
     */
    public List<GlobalRole> findAllActiveRoles() {
        return globalRoleRepository.findAll().stream()
                .filter(GlobalRole::isActive)
                .toList();
    }

    /**
     * Obtiene la descripción de un rol.
     *
     * @param roleName El nombre del rol.
     * @return La descripción del rol.
     */
    private String getRoleDescription(EmployeeRole roleName) {
        switch (roleName) {
            case ROLE_USER:
                return "Usuario básico con permisos limitados";
            case ROLE_MODERATOR:
                return "Moderador con permisos de gestión de contenido";
            case ROLE_ADMIN:
                return "Administrador con permisos completos en su tenant";
            case ROLE_SUPER_ADMIN:
                return "Super administrador con permisos globales del sistema";
            default:
                return "Rol del sistema";
        }
    }
}
