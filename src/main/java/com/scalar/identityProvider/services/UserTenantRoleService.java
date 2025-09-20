package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.EmployeeRole;
import com.scalar.identityProvider.models.GlobalRole;
import com.scalar.identityProvider.models.UserTenantRole;
import com.scalar.identityProvider.repository.UserTenantRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para manejar operaciones relacionadas con roles de usuario por tenant.
 */
@Service
public class UserTenantRoleService {

    @Autowired
    private UserTenantRoleRepository userTenantRoleRepository;

    @Autowired
    private GlobalRoleService globalRoleService;

    /**
     * Asigna roles a un usuario en un tenant específico.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @param roleNames Los nombres de los roles a asignar.
     * @return La asignación de roles creada o actualizada.
     */
    public UserTenantRole assignRolesToUser(String userId, String tenantId, Set<String> roleNames) {
        Optional<UserTenantRole> existingAssignment = userTenantRoleRepository
                .findByUserIdAndTenantId(userId, tenantId);

        UserTenantRole userTenantRole;
        if (existingAssignment.isPresent()) {
            userTenantRole = existingAssignment.get();
        } else {
            userTenantRole = new UserTenantRole(userId, tenantId);
        }

        Set<GlobalRole> roles = new HashSet<>();
        for (String roleName : roleNames) {
            EmployeeRole employeeRole = mapStringToEmployeeRole(roleName);
            Optional<GlobalRole> globalRole = globalRoleService.findByName(employeeRole);
            globalRole.ifPresent(roles::add);
        }

        userTenantRole.setRoles(roles);
        return userTenantRoleRepository.save(userTenantRole);
    }

    /**
     * Obtiene los roles de un usuario en un tenant específico.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @return Un Optional que contiene la asignación de roles si existe.
     */
    public Optional<UserTenantRole> getUserRolesInTenant(String userId, String tenantId) {
        return userTenantRoleRepository.findByUserIdAndTenantId(userId, tenantId);
    }

    /**
     * Obtiene todos los tenants donde un usuario tiene roles asignados.
     *
     * @param userId El ID del usuario.
     * @return Lista de asignaciones de roles del usuario.
     */
    public List<UserTenantRole> getUserTenants(String userId) {
        return userTenantRoleRepository.findByUserId(userId);
    }

    /**
     * Obtiene todos los usuarios con roles en un tenant específico.
     *
     * @param tenantId El ID del tenant.
     * @return Lista de asignaciones de roles en el tenant.
     */
    public List<UserTenantRole> getTenantUsers(String tenantId) {
        return userTenantRoleRepository.findByTenantId(tenantId);
    }

    /**
     * Verifica si un usuario tiene un rol específico en un tenant.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @param roleName El nombre del rol.
     * @return true si el usuario tiene el rol, false en caso contrario.
     */
    public boolean userHasRoleInTenant(String userId, String tenantId, EmployeeRole roleName) {
        Optional<UserTenantRole> userTenantRole = getUserRolesInTenant(userId, tenantId);
        if (userTenantRole.isPresent()) {
            return userTenantRole.get().getRoles().stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        }
        return false;
    }

    /**
     * Elimina la asignación de roles de un usuario en un tenant.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     */
    public void removeUserFromTenant(String userId, String tenantId) {
        Optional<UserTenantRole> userTenantRole = userTenantRoleRepository
                .findByUserIdAndTenantId(userId, tenantId);
        userTenantRole.ifPresent(role -> userTenantRoleRepository.delete(role));
    }

    /**
     * Mapea un string a un EmployeeRole.
     *
     * @param roleName El nombre del rol como string.
     * @return El EmployeeRole correspondiente.
     */
    private EmployeeRole mapStringToEmployeeRole(String roleName) {
        switch (roleName.toLowerCase()) {
            case "user":
                return EmployeeRole.ROLE_USER;
            case "mod":
            case "moderator":
                return EmployeeRole.ROLE_MODERATOR;
            case "admin":
                return EmployeeRole.ROLE_ADMIN;
            case "super_admin":
            case "superadmin":
                return EmployeeRole.ROLE_SUPER_ADMIN;
            default:
                return EmployeeRole.ROLE_USER;
        }
    }
}
