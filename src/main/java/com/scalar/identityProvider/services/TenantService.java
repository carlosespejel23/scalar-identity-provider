package com.scalar.identityProvider.services;

import com.scalar.identityProvider.models.Tenant;
import com.scalar.identityProvider.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar operaciones relacionadas con tenants.
 */
@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Crea un nuevo tenant.
     *
     * @param tenant El tenant a crear.
     * @return El tenant creado.
     */
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    /**
     * Busca un tenant por su ID.
     *
     * @param id El ID del tenant.
     * @return Un Optional que contiene el tenant si se encuentra.
     */
    public Optional<Tenant> findById(String id) {
        return tenantRepository.findById(id);
    }

    /**
     * Busca un tenant por su tenantId.
     *
     * @param tenantId El tenantId del tenant.
     * @return Un Optional que contiene el tenant si se encuentra.
     */
    public Optional<Tenant> findByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }

    /**
     * Obtiene todos los tenants activos.
     *
     * @return Lista de todos los tenants activos.
     */
    public List<Tenant> findAllActiveTenants() {
        return tenantRepository.findAll().stream()
                .filter(Tenant::isActive)
                .toList();
    }

    /**
     * Verifica si un tenantId ya existe.
     *
     * @param tenantId El tenantId a verificar.
     * @return true si existe, false en caso contrario.
     */
    public boolean existsByTenantId(String tenantId) {
        return tenantRepository.existsByTenantId(tenantId);
    }

    /**
     * Verifica si un nombre de tenant ya existe.
     *
     * @param name El nombre a verificar.
     * @return true si existe, false en caso contrario.
     */
    public boolean existsByName(String name) {
        return tenantRepository.existsByName(name);
    }

    /**
     * Actualiza un tenant.
     *
     * @param tenant El tenant a actualizar.
     * @return El tenant actualizado.
     */
    public Tenant updateTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    /**
     * Elimina un tenant por su ID.
     *
     * @param id El ID del tenant a eliminar.
     */
    public void deleteTenant(String id) {
        tenantRepository.deleteById(id);
    }
}
