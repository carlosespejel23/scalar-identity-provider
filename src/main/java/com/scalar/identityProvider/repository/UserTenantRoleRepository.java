package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.UserTenantRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos relacionadas con UserTenantRole.
 */
public interface UserTenantRoleRepository extends MongoRepository<UserTenantRole, String> {

    /**
     * Busca la asignación de roles de un usuario en un tenant específico.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @return Un Optional que contiene la asignación si se encuentra.
     */
    Optional<UserTenantRole> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Busca todas las asignaciones de roles de un usuario.
     *
     * @param userId El ID del usuario.
     * @return Lista de asignaciones de roles del usuario.
     */
    List<UserTenantRole> findByUserId(String userId);

    /**
     * Busca todas las asignaciones de roles en un tenant específico.
     *
     * @param tenantId El ID del tenant.
     * @return Lista de asignaciones de roles en el tenant.
     */
    List<UserTenantRole> findByTenantId(String tenantId);

    /**
     * Verifica si un usuario tiene roles asignados en un tenant.
     *
     * @param userId El ID del usuario.
     * @param tenantId El ID del tenant.
     * @return true si tiene roles asignados, false en caso contrario.
     */
    boolean existsByUserIdAndTenantId(String userId, String tenantId);
}
