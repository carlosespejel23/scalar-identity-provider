package com.scalar.identityProvider.repository;

import com.scalar.identityProvider.models.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos relacionadas con Tenant.
 */
public interface TenantRepository extends MongoRepository<Tenant, String> {

    /**
     * Busca un tenant por su tenantId.
     *
     * @param tenantId El ID del tenant.
     * @return Un Optional que contiene el Tenant si se encuentra, o vacío si no se encuentra.
     */
    Optional<Tenant> findByTenantId(String tenantId);

    /**
     * Verifica si un tenantId ya existe en la base de datos.
     *
     * @param tenantId El tenantId a verificar.
     * @return Un Boolean que indica si el tenantId existe (true) o no (false).
     */
    Boolean existsByTenantId(String tenantId);

    /**
     * Verifica si un nombre de tenant ya existe en la base de datos.
     *
     * @param name El nombre del tenant a verificar.
     * @return Un Boolean que indica si el nombre existe (true) o no (false).
     */
    Boolean existsByName(String name);
}
